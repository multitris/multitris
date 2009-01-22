require 'socket'

class MultitrisClients

	def initialize(server, port)
		@server= server
		@port= port
		@connections= Hash.new
		@queues= Hash.new
	end

	def userAdd(cookie)
		@queues[cookie]= []
		begin
			@connections[cookie]= TCPSocket.new(@server, @port)
		rescue Errno::ECONNREFUSED
			userClose(cookie, "no server found");
		end
		Thread.new do
			begin
				@connections[cookie].each do |line|
					line.strip!
					if line=~ /^FUCKYOU( (.*?))?$/
						userClose(cookie, $2 || "");
					else
						@queues[cookie] << line
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
		@connections[cookie].puts(string)
	end

	def userReceive(cookie)
		result= @queues[cookie].shift || ""
		if @connections[cookie].closed? and @queues[cookie].size == 0
			@connections.delete(cookie)
			@queues.delete(cookie)
		end
		result
	end

	def userClose(cookie, reason= "")
		@queues[cookie] << "FUCKYOU " + reason
		begin # FUCKYOU before close, so other threads won't delete this user to early
			@connections[cookie].close
		rescue IOError
		end
	end

end
