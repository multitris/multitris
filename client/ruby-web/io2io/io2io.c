/*
 * Author:: Johannes Krude
 * Copyright:: (c) Johannes Krude 2008
 * License:: AGPL3
 *
 * This file is part of multitris.
 *
 * multitris is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * multitris is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with multitris.  If not, see <http://www.gnu.org/licenses/>.
 */

#include <ruby.h>
#include <errno.h>
#include <fcntl.h>
#include <sys/sendfile.h>

#define SENDFILE_MAX 16777216 // 16MB

/*
 * Read data from in and write it to out, until the end of
 * in is reached. in and out must be given as Integer.
 */
VALUE t_forever(VALUE self, VALUE in, VALUE out)
{
	int s, d;
	s= NUM2INT(in);
	d= NUM2INT(out);

	//deactivate O_NONBLOCK
	int flags_s= fcntl(s, F_GETFL);
	int flags_d= fcntl(d, F_GETFL);
	fcntl(s, F_SETFL, 0);
	fcntl(d, F_SETFL, 0);

	char buffer[1024];
	int size;
	while ((size= read(s, buffer ,sizeof(buffer)))>0) {
		if (size==-1)
			rb_sys_fail("io2io input");
		if (write(d, buffer, size)==-1)
			rb_sys_fail("io2io output");
	}

	fcntl(s, F_SETFL, flags_s);
	fcntl(d, F_SETFL, flags_d);

	return Qnil;
}

size_t do_sendfile(int dest, int source, size_t size)
{
	int last;
	if ((last= sendfile(dest, source, NULL, size)) == -1)
		rb_sys_fail("sendfile");
	return last;
}

#define min(a, b) (a < b ? a : b)

/*
 * Use the sendfile systemcall to move size bytes data from
 * source to dest. This works only if source is a regular
 * file and dest is a socket. If size is -1 data will be
 * moved until the end of source is reached. Source and dest
 * must be given as Integer.
 */
VALUE t_sendfile(VALUE self, VALUE in, VALUE out, VALUE size)
{
	int s, d;
	int si;
	s= NUM2INT(in);
	d= NUM2INT(out);
	si= NUM2INT(size);

	//deactivate O_NONBLOCK
	int flags_s= fcntl(s, F_GETFL);
	int flags_d= fcntl(d, F_GETFL);
	fcntl(s, F_SETFL, 0);
	fcntl(d, F_SETFL, 0);

	size_t last;
	size_t send= 0;
	if (size == -1) { // send everything
		while ((last= do_sendfile(d, s, SENDFILE_MAX)) < 0)
			send+= last;
	} else { // send only the bytes requested
		while (si > 0) {
			last= do_sendfile(d, s, min(si, SENDFILE_MAX));
			send+= last;
			if (last == 0) // the end of the file
				break;
		}
	}

	fcntl(s, F_SETFL, flags_s);
	fcntl(d, F_SETFL, flags_d);

	return INT2NUM(send);
}

VALUE M_io2io;

/*
 * Some functions to put data to the network with high performance.
 */
void Init_io2io()
{
	M_io2io= rb_define_module("IO2IO");
	rb_define_module_function(M_io2io, "forever", t_forever, 2);
	rb_define_module_function(M_io2io, "sendfile", t_sendfile, 3);
}
