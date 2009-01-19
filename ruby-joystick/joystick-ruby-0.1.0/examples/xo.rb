#!/usr/bin/ruby

require 'curses'
require 'joystick'

# make sure a device was specified on the command-line
unless ARGV.size > 0
  $stderr.puts 'Missing device name.'
  exit -1
end

# get joystick
joy = Joystick::Device::open ARGV[0]

# curses signal handler
def on_sig sig 
  Curses::close_screen
  exit sig
end

# set up signal handlers
(1..15).each { |i| trap(i) { |sig| on_sig sig } if trap(i, 'SIG_IGN') != 0 }

# set up curses canvas
Curses::init_screen
Curses::nl
Curses::noecho
srand

# get screen width and height
w = Curses::cols - 2
h = Curses::lines - 2

Point = Struct::new(:x, :y)

# set up user
user = Point.new(w / 2, h / 2)
Curses::setpos(user.y, user.x)
Curses::addstr('X')

points = []

# loop forever
loop { 
  if joy.pending?
    ev = joy.ev

    case ev.type
    when Joystick::Event::AXIS
      # erase old player
      Curses::setpos(user.y, user.x)
      Curses::addstr(points.include?(user) ? 'O' : ' ')

      # handle joystick event
      if ev.num % 2 == 1
        if ev.value > 0
          user.y += 1
        elsif ev.value < 0
          user.y -= 1
        end
      else
        if ev.value > 0
          user.x += 1
        elsif ev.value < 0
          user.x -= 1
        end
      end

      # boundary checks
      user.x = 1 if user.x > w - 1
      user.x = w - 1 if user.x < 1
      user.y = 1 if user.y > h - 1
      user.y = h - 1 if user.y < 1

      # redraw user
      Curses::setpos(user.y, user.x)
      Curses::addstr('X')

      # print coords
      Curses::setpos(1, 1)
      Curses::addstr("(#{user.x}, #{user.y})")

      # print exit info
      Curses::setpos(h, 1)
      Curses::addstr('Press Ctrl-C to exit.')
    when Joystick::Event::BUTTON
      # add the current point ot the list of saved points
      points << user.dup
    end

  end

  # refresh the screen
  Curses::refresh
  sleep 0.0
}
