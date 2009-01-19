#include <linux/joystick.h>
#include <sys/select.h>
#include <sys/time.h>
#include <sys/types.h>
#include <fcntl.h>
#include <unistd.h>
#include <string.h>
#include <errno.h>
#include <ruby.h>

#define UNUSED(a) ((void) (a))
#define VERSION "0.1.0"
#define MAX_JOYSTICKS 32

static VALUE mJoy,
             cDev,
             cEv;

static struct js_event evs[MAX_JOYSTICKS];

static void dont_free(void *ptr) { UNUSED(ptr); }

/****************************/
/* Joystick::Device methods */
/*******************(********/
static void dev_free(void *ptr) {
  int *fd = (int*) ptr;
  if (fd && *fd >= 0)
    close(*fd);
  free(fd);
}

/*
 * Close a Joystick::Device object.
 *
 * Note: You cannot read or write to a closed joystick device.
 *
 * Example:
 *   joy.close
 *
 */
static VALUE j_dev_close(VALUE self) {
  int *fd;

  Data_Get_Struct(self, int, fd);
  if (fd && *fd >= 0) {
    close(*fd);
    *fd = -1;
  }

  return Qnil;
}

/*
 * Open and allocate a new Joystick::Device object.
 *
 * This method also accepts a block.
 *
 * Aliases:
 *   Joystick::Device::open
 * 
 * Examples:
 *   joy = Joystick::Device.new '/dev/input/js0'
 *
 *   # read a joystick device in block mode
 *   Joystick::Device::open('/dev/input/js0') { |joy|
 *     # ... do stuff ...
 *   }
 *
 */
VALUE j_dev_new(VALUE klass, VALUE path) {
  VALUE self = Qnil;
  int *fd, block_given;

  block_given = rb_block_given_p();
  if ((fd = malloc(sizeof(int))) != NULL) {
    if ((*fd = open(RSTRING(path)->ptr, O_RDONLY)) >= 0) {
      if (*fd >= MAX_JOYSTICKS) 
        rb_raise(rb_eException, "Exceeded maximum joystick descriptor.");

      self = Data_Wrap_Struct(klass, 0, dev_free, fd);
      rb_obj_call_init(self, 0, NULL);

      if (block_given) {
        rb_yield(self);
        j_dev_close(self);
        self = Qnil;
      }
    } else if (!block_given) {
      rb_raise(rb_eException, "Couldn't open device: %s", strerror(errno));
    }
  } else if (!block_given) {
    rb_raise(rb_eException, "Couldn't allocate file descriptor.");
  }

  return self;
}

/* 
 * Constructor for Joystick::Device.
 * 
 * Note: this method should never be called directly.
 *
 */
static VALUE j_dev_init(VALUE self) {
  return self;
}

static VALUE j_dev_pending(VALUE self) {
  static struct timeval tv;
  static fd_set fds;
  int *fd;
  
  Data_Get_Struct(self, int, fd);
  if (fd && *fd >= 0) {
    tv.tv_sec = 0; tv.tv_usec = 0;
    FD_ZERO(&fds); FD_SET(*fd, &fds);

    if (select(*fd + 1, &fds, NULL, NULL, &tv) > 0 &&
        FD_ISSET(*fd, &fds))
      return Qtrue;
  }

  return Qfalse;
}

/*
 * Return the next Joystick::Event from a Joystick::Device object.
 *
 * Note: For performance reasons, only one joystick event is allocated
 * per device.  Calling this method will silently overwrite old events
 * for this device.
 *  
 * This method will block if no data is waiting to be read, so you
 * should probably test the Joystick::Device object with
 * Joystick::Device#pending? before calling this method.
 *
 *
 * Aliases:
 *   Joystick::Device::ev
 *   Joystick::Device::event
 *   Joystick::Device::next_ev
 *
 * Example:
 *   ev = joy.next_event
 *
 */
static VALUE j_dev_ev(VALUE self) {
  int *fd;

  Data_Get_Struct(self, int, fd);

  if (fd && *fd >= 0)
    if (read(*fd, &evs[*fd], sizeof(struct js_event)) > 0)
      return Data_Wrap_Struct(cEv, 0, dont_free, fd);

  return Qnil;
}

/*
 * Return the number of axes for this joystick.
 *
 * Note: Don't forget that each joypad has two axes.
 * 
 * Aliases:
 *   Joystick::Device#num_axes
 *
 * Example:
 *   puts "This joystick has #{joy.axes / 2} pads."
 * 
 */
static VALUE j_dev_axes(VALUE self) {
  int *fd;

  Data_Get_Struct(self, int, fd);

  if (fd && *fd >= 0) {
    unsigned char axes;
    if (ioctl(*fd, JSIOCGAXES, &axes) == -1)
      rb_raise(rb_eException, "axes ioctl() error: %s", strerror(errno));
    return INT2FIX(axes);
  }

  return INT2FIX(-1);
}

/*
 * Return the number of buttons for this Joystick::Device object.
 *
 * Aliases:
 *   Joystick::Device#num_buttons
 *
 * Example:
 *   puts "This joystick has #{joy.buttons / 2} buttons."
 * 
 */
static VALUE j_dev_buttons(VALUE self) {
  int *fd;

  Data_Get_Struct(self, int, fd);

  if (fd && *fd >= 0) {
    unsigned char btns;
    if (ioctl(*fd, JSIOCGBUTTONS, &btns) == -1)
      rb_raise(rb_eException, "btns ioctl() error: %s", strerror(errno));
    return INT2FIX(btns);
  }

  return INT2FIX(-1);
}

/*
 * Return the name of this Joystick::Device object.
 *
 * Note: not all joysticks have descriptive names.
 *
 * Example:
 *   puts "Joystick Name: #{joy.name}"
 *
 */
static VALUE j_dev_name(VALUE self) {
  int *fd;

  Data_Get_Struct(self, int, fd);

  if (fd && *fd >= 0) {
    static char name[256];
    memset(name, 0, sizeof(name));
    if (ioctl(*fd, JSIOCGNAME(sizeof(name) - 1), name) == -1)
      rb_raise(rb_eException, "name ioctl() error: %s", strerror(errno));
    return rb_str_new2(name);
  }

  return Qnil;
}

/*
 * Return the file descriptor of this Joystick::Device object.
 *
 * Example:
 *   fd = joy.fd
 *
 */
static VALUE j_dev_fd(VALUE self) {
  int *fd;

  Data_Get_Struct(self, int, fd);
  if (fd && *fd >= 0)
    return INT2FIX(*fd);

  return Qnil;
}


/***************************/
/* Joystick::Event methods */
/***************************/

/*
 * Constructor for Joystick::Event objects.
 *
 * Note: you should never call this method directly.
 *
 */
static VALUE j_ev_init(VALUE self) {
  return self;
}

/* 
 * Return the timestamp (in milliseconds) of this Joystick::Event.
 *
 * Example:
 *   stamp = ev.time
 *
 */
static VALUE j_ev_time(VALUE self) {
  int *fd;
  Data_Get_Struct(self, int, fd);
  return INT2FIX((fd && *fd >= 0) ? evs[*fd].time : -1);
}

/* 
 * Return the value of this Joystick::Event object.
 *
 * The content of Joystick::Event#value varies depending on the type of
 * event.  For Joystick::Event::AXIS events, the value is the direction
 * (and optionally magnitude) of the axis (eg, -32767 for maximum
 * right/up, 19931 for partial left/down, etc).  For
 * Joystick::Event:BUTTON events, the value is usually 1 or 0, for
 * button presses and button releases, respectively.
 * 
 * Aliases:
 *   Joystick::Event#val
 *
 * Example:
 *   puts "The button was " << 
 *        ((ev.value != 0) ? 'pressed' : 'released') <<
 *        "." if ev.type == Joystick::Event::BUTTON
 *
 */
static VALUE j_ev_value(VALUE self) {
  int *fd;
  Data_Get_Struct(self, int, fd);
  return INT2FIX((fd && *fd >= 0) ? evs[*fd].value : -1);
}

/* 
 * Return the type of this Joystick::Event object.
 *
 * Values:
 *   Joystick::Event::INIT
 *   Joystick::Event::BUTTON
 *   Joystick::Event::AXIS
 *
 * Example:
 *   case ev.type
 *   when Joystick::Event::INIT
 *     puts 'init'
 *   when Joystick::Event::BUTTON
 *     puts "button: #{ev.num}, #{ev.val}"
 *   when Joystick::Event::AXIS
 *     puts "axis: #{ev.num}, #{ev.val}"
 *   end
 *
 */
static VALUE j_ev_type(VALUE self) {
  int *fd;
  Data_Get_Struct(self, int, fd);
  return INT2FIX((fd && *fd >= 0) ? evs[*fd].type : -1);
}

/* 
 * Return the number of this Joystick::Event object.
 *
 * The value of Joystick::Event#number is the axis or button of the
 * event.
 *
 * Aliases:
 *   Joystick::Event#num
 *
 * Example:
 *   puts "You're using joypad #{(ev.num / 2) + 1}." \
 *     if ev.type == Joystick::Event::AXIS
 *
 */
static VALUE j_ev_number(VALUE self) {
  int *fd;
  Data_Get_Struct(self, int, fd);
  return INT2FIX((fd && *fd >= 0) ? evs[*fd].number : -1);
}

void Init_joystick(void) {
  mJoy = rb_define_module("Joystick");
  rb_define_const(mJoy, "JOYSTICK_VERSION", rb_str_new2(VERSION));
  rb_define_const(mJoy, "VERSION", INT2FIX(JS_VERSION));

  /***********************/
  /* define Device class */
  /***********************/
  cDev = rb_define_class_under(mJoy, "Device", rb_cObject);
  rb_define_singleton_method(cDev, "new", j_dev_new, 1);
  rb_define_singleton_method(cDev, "open", j_dev_new, 1);
  rb_define_singleton_method(cDev, "initialize", j_dev_init, 0);

  rb_define_method(cDev, "pending?", j_dev_pending, 0);

  rb_define_method(cDev, "next_event", j_dev_ev, 0);
  rb_define_alias(cDev, "next_ev", "next_event");
  rb_define_alias(cDev, "event", "next_event");
  rb_define_alias(cDev, "ev", "next_event");

  rb_define_method(cDev, "axes", j_dev_axes, 0);
  rb_define_alias(cDev, "num_axes", "axes");

  rb_define_method(cDev, "buttons", j_dev_buttons, 0);
  rb_define_alias(cDev, "num_buttons", "buttons");

  rb_define_method(cDev, "name", j_dev_name, 0);
  
  rb_define_method(cDev, "fd", j_dev_fd, 0);
  rb_define_alias(cDev, "descriptor", "fd");
  rb_define_alias(cDev, "file_descriptor", "fd");
  
  rb_define_method(cDev, "close", j_dev_close, 0);

  /**********************/
  /* define Event class */
  /**********************/
  cEv = rb_define_class_under(mJoy, "Event", rb_cObject);
  rb_define_singleton_method(cEv, "initialize", j_ev_init, 0);

  rb_define_method(cEv, "time", j_ev_time, 0);
  rb_define_method(cEv, "value", j_ev_value, 0);
  rb_define_alias(cEv, "val", "value");
  rb_define_method(cEv, "type", j_ev_type, 0);
  rb_define_method(cEv, "number", j_ev_number, 0);
  rb_define_alias(cEv, "num", "number");

  rb_define_const(cEv, "BUTTON", INT2FIX(JS_EVENT_BUTTON));
  rb_define_const(cEv, "AXIS", INT2FIX(JS_EVENT_AXIS));
  rb_define_const(cEv, "INIT", INT2FIX(JS_EVENT_INIT));
}

