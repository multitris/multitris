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
#include <magic.h>
#include <string.h>

magic_t cookie;

/*
 * Return the mimetype of the file given with filename.
 */
VALUE t_file(VALUE self, VALUE input)
{
	//SaveStringValue(input);
	char *result= magic_file(cookie, StringValueCStr(input));
	int errno;
	if (errno= magic_errno(cookie)) {
		rb_raise(rb_mErrno, errno);
		return Qnil;
	}
	char *error;
	if (error= magic_error(cookie)) {
		rb_raise(rb_eException, error);
		return Qnil;
	}
	return rb_str_new2(result);
}

VALUE M_magicmime;

/*
 * Determine the mime type with the file util.
 */
void Init_magicmime()
{
	cookie= magic_open(MAGIC_SYMLINK | MAGIC_MIME_TYPE);
	magic_load(cookie, 0);
	M_magicmime= rb_define_module("MagicMime");
	rb_define_module_function(M_magicmime, "file", t_file, 1);
}
