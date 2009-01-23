#
# Author:: Johannes Krude
# Copyright:: (c) Johannes Krude 2008
# License:: AGPL3
#
#--
# This file is part of multitris.
#
# multitris is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# multitris is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with multitris.  If not, see <http://www.gnu.org/licenses/>.
#++

require 'socket'

module Multitris

Version= "0.1"

	# A Multitris client
	class Client

		# Connects to a multitris server. It yields for
		# messages which should be handled by the caller. It
		# returns the messages the server states as reason
		# for disconnecting.
		def connect(server, port, player= nil)
			begin
				@connection= TCPSocket.new(server, port)
				@connection.print "IWANTFUN #{Version} #{player or `whoami`.strip}\n"
				@connection.each do |line|
					line.strip!
					case line
					when /^CHUCK (\d+)$/ #ping
						@connection.print "NORRIS #{$1}\n"
					when /^FUCKYOU( .+?)$/
						@connection.close
						return $1
					when /^(ATTENTION \w\w\w\w\w\w \d+)|(GOFORREST)|(PLONK)|(NOTBAD)|(THATWASMISERABLE)$/
						yield line if block_given?
					end
				end
			rescue IOError
				return
			end
		end

		# Close the connection to the server
		def close
			@connection.close
		end

		# Move the tetris block one to the left.
		def left
			@connection.print "LAFONTAINE\n"
		end

		# Move the tetris block one to the right.
		def right
			@connection.print "STOIBER\n"
		end

		# Move up.
		def up
			@connection.print "MARIHUANA\n"
		end

		# Move down.
		def down
			@connection.print "MOELLEMANN\n"
		end

		# Turn the tetris block
		def turn
			@connection.print "TURN\n"
		end

	end

end
