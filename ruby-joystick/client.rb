require 'socket'

module Multitris

Version= "1.0"

	# A Multitris client
	class Client

		# Connects to a multitris server. It yields for
		# messages which should be handled by the caller. It
		# returns the messages the server states as reason
		# for disconnecting.
		def connect(server, port)
			@connection= TCPSocket.new(server, port)
			@connection.print "IWANTFUN #{Version}\n"
			@connection.each do |line|
				line.strip!
				case line
				when /^CHUCK (/d+)$/ #ping
					@connection.print "NORRIS #{$1}\n"
				when /^FUCKYOU( .+?)$/
					@connection.close
					return $1
				when /^(ATTENTION \w\w\w\w\w\w \d+)|(GOFORREST)|(PLONK)|(NOTBAD)|(THATWASMISERABLE)$/
					yield line if block_given?
				end
			end
		end

		# Move the tetris block one to the left.
		def left
			@connection.print "LAFONTAINE\n"
		end

		# Move the tetris block one to the right.
		def right
			@connection.print "STOIBER\n"
		end

		# Turn the tetris block
		def turn
			@connection.print "TURN\n"
		end

	end

end
