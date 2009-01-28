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

require 'multitris/comand'

module Multitris

	# A ComandSequence is some kind of io where Comands are
	# transmitted. ComandSequence has a update method for the
	# observer pattern. If update is called the argument of update
	# will be transmitted.
	class ComandSequence

		# io is the IO object the Comands will be
		# transmitted over. If a block is given, the block
		# will be called for every Comand which is received
		# over the ComandSequence. If the block returns false
		# the Comand can be also recieved with the recieve
		# method.
		def initialize(io)
			@io= io
			@queue, input= IO.pipe
			@mutex_transmit= Mutex.new
			@mutex_receive= Mutex.new
			Thread.new do
				begin
					@io.each do |str|
						str.strip!
						cmd= Comand.from_string(str)
						if cmd === :chuck
							cmd.name= :norris
							transmit(cmd)
						else
							input.puts cmd unless block_given? and yield(cmd)
						end
					end
					begin
						@io.close
					rescue IOError
					end
				rescue Errno::ECONNRESET
					begin
						@io.close
					rescue IOError
					end
				end
			end
		end

		# Transmit a Comand over the ComandSequence. Transmit
		# is thread save. No two Comand's will be mixed
		# together while transmitting.
		def transmit(cmd)
			@mutex_transmit.synchronize do
				begin
					@io.puts cmd.to_s
				rescue IOError
					begin
						@io.close
					rescue IOError
					end
				end
			end
		end

		# this is for the observer pattern
		alias :update :transmit

		# Receive a Comand from the ComandSequence. Receive is
		# thread. If one thread is still reading, the others
		# will wait.
		def receive
			@mutex_receive.synchronize do
				Comand.from_string(@queue.readline.strip)
			end
		end

		# Wether the io is closed
		def closed?
			@io.closed?
		end

	end

end
