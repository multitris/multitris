#
# Author:: Johannes Krude
# Copyright:: (c) Johannes Krude 2008
# License:: AGPL3
#
#--
# This file is part of sendhttp.
#
# sendhttp is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# sendhttp is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with sendhttp.  If not, see <http://www.gnu.org/licenses/>.
#++

require 'sendhttp-lib/io2io'
require 'sendhttp-lib/magicmime'
require 'socket'

# A small but not complete http server.
#
# HTTPServer handles:
# * listening for connections
# * parsing http header variables
# * extracting multipart form-data
# * generating http header variables for responses
#
# HTTPServer does not handle:
# * deciding which content to send to the client
# * access control
class HTTPServer

	def initialize
		#@files= [] FIXME I don't remember what this was for
	end

	# Listens on port PORT and call's handle_connection with the
	# connection and BLOCK for every incoming connection.
	def listen(port, &block)
		@listen= TCPServer.new(nil, port)
		begin
			threads= 0
			while connection= @listen.accept
				threads+= 1
				Thread.new(connection) do |c|
					handle_connection(c, &block)
					threads-= 1
				end
			end
		rescue Errno::EAGAIN
			retry
		rescue Errno::EINVAL
		end
		sleep 0.01 while threads > 0
	end

	# Stop listening for incoming connections. This causes
	# listen to return when all active connections have
	# terminated.
	def close
		@listen.close_read
	end

	# handle_connection reads the HTTP header from the connection
	# and call's block if it is a valid request.
	# block.call(filename, vars) is given the filename requested
	# and all http vars as a Hash. If the client sends data as
	# multipart form-data, this data is a part of vars as
	# vars["form-data"]. The block should either return nil, an
	# IO object, an array [IO, Hash], or an array
	# [IO, Hash, String]. The String is the http status code send
	# to the browser, defaults to "200 OK" the Hash are vars which
	# are send in the http header, and the IO is the source for
	# the http body. In case of nil the http status code will be
	# "404 Bad Request".
	def handle_connection(connection, &block)
		if (if connection.readline.strip=~ /^(GET|POST) \/(.*?) HTTP\/1.[01]$/
			filename= $2
			vars= HTTPServer.read_vars(connection)
			if vars["Content-Type"]=~ /^multipart\/form-data;\s+boundary=(.+?)$/
				vars["form-data"]= HTTPServer.read_multipart(connection, "--#{$1}")
			end
			true
		end) and stream= block.call(filename, vars)
			stream= [stream].flatten
			status= ((stream.size == 3 and stream.pop) or "200 OK")
			vars= ((stream.size == 2 and stream.pop) or Hash.new)
			stream= stream[0]
			connection.puts "HTTP/1.1 #{status}"
			connection.puts HTTPServer.header.collect { |name, value| "#{name}: #{value}" }
			connection.puts vars.collect { |name, value| "#{name}: #{value}" }
			connection.puts "Connection: close"
			connection.puts
			case stream
			when File
				IO2IO.sendfile(stream.to_i, connection.to_i, (vars["Content-Length"] or -1))
			when IO
				IO2IO.forever(stream.to_i, connection.to_i)
			when String
				connection.write stream
			end
			connection.close
		else
			connection.puts("HTTP/1.1 400 Bad Request")
			connection.puts HTTPServer.header.collect { |name, value| "#{name}: #{value}" }
			connection.puts
			connection.close
		end
	end

	# Returns the default http headers as a Hash.
	def HTTPServer.header
		result= Hash.new
		result["Date"]= "Date: #{Time.now.gmtime.strftime("%a, %d %b %Y %H:%M:%S GMT")}"
		result["Server"]= "sendhttp/#{$version}"
		return result
	end

	# Read all http header variables from an IO, the IO should be
	# seeked immediately behind "GET /filename HTTP/1.1\n".
	def HTTPServer.read_vars(stream)
		result= Hash.new
		while stream.readline.strip=~ /^([^:]+):(.*)$/
			result[$1]= $2.strip
		end
		result
	end

	# Seek behind the next boundary in stream. Returns an array
	# [content, end?], content contains the content of stream
	# between the current position and the boundary. end? is
	# true if the found boundary was an multipart end
	# boundary.
	def HTTPServer.find_boundary(stream, boundary)
		boundary2= "#{boundary}--"
		result= ""
		the_end= false
		until (line= stream.readline).strip == boundary or (line.strip == boundary2 and the_end= true)
			result+= line
		end
		[result.sub(/\r\n$/, ""), the_end] # remove the newline necessary to find the boundary
	end

	# Reads a multipart form-data part from stream. The stream can
	# be seeked anywhere before the beginning boundary of the
	# multipart form-data. Returns a Hash of all the variables
	# from the multipart form-data.
	def HTTPServer.read_multipart(stream, boundary)
		result= Hash.new
		tmp, the_end= find_boundary(stream, boundary)
		until the_end
			vars= read_vars(stream)
			if vars["Content-Disposition"]=~ /^form-data;?/
				vars2= Hash.new
				while $'.lstrip=~ /(\w+)="(.*?)"(;|$)/ # be careful this does not work for name="";";, in our case we only use "name" and "";" is never a part of name, but if we want to use for example filename unexpected stuff could happen
					vars2[$1]= $2
				end
				result[vars2["name"]], the_end= find_boundary(stream, boundary)
			else
				tmp, the_end= find_boundary(stream, boundary)
			end
		end
		result
	end
	
	# Return the return value of HTTPServer.file in the block
	# given to listen or handle_connection to send the file
	# filename to the http client.
	def HTTPServer.put_file(filename)
		vars= Hash.new
		vars["Content-Type"]= MagicMime.file(filename)
		vars["Content-Length"]= File.size(filename)
		return [File.open(filename), vars]
	end

end

