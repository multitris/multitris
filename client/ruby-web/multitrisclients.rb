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

class MultitrisClients

	def initialize(server, port)
		@server= server
		@port= port
		@connections= Hash.new
		@queues= Hash.new
		@mutex_transmit= Hash.new
		@mutex_recieve= Hash.new
	end

	def userAdd(cookie)
		@queues[cookie]= []
		@mutex_transmit[cookie]= Mutex.new
		@mutex_recieve[cookie]= Mutex.new
		begin
			@connections[cookie]= TCPSocket.new(@server, @port)
		rescue Errno::ECONNREFUSED
			userClose(cookie, "no server found");
			return
		end
		Thread.new do
			begin
				@connections[cookie].each do |line|
					line.strip!
					if line=~ /^FUCKYOU( (.*?))?$/
						userClose(cookie, $2 || "");
					else
						@mutex_recieve[cookie].synchronize do
							queue= @queues[cookie]
							queue<< line if queue
						end
					end
				end
			rescue
				userClose(cookie, "connection unexpected lost")
			end
		end
	end

	def userExists?(cookie)
		@connections[cookie] != nil
	end

	def userTransmit(cookie, string)
		begin
			@mutex_transmit[cookie].synchronize do
				@connections[cookie].puts(string)
				if string=~ /^FUCKYOU( (.*?))?$/
					@connections[cookie].close
				end
			end
		rescue Errno::EPIPE
			userCLose(cookie, "connection unexpected lost");
		end
	end

	def userReceive(cookie)
		result= nil
		@mutex_recieve[cookie].synchronize do
			result= @queues[cookie].shift || ""
			if @connections[cookie] and @connections[cookie].closed? and @queues[cookie] and @queues[cookie].size == 0
				@connections.delete(cookie)
				@queues.delete(cookie)
				@mutex.delete(cookie)
			end
		end
		result
	end

	def userClose(cookie, reason= "")
		@mutex_recieve[cookie].synchronize do
			queue= @queues[cookie]
			queue<< "FUCKYOU " + reason if queue
			begin # FUCKYOU before close, so other threads won't delete this user to early
				@connections[cookie].close if @connections[cookie]
			rescue IOError
			end
		end
	end

end
