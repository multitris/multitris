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

require 'multitris/comandsequence'
require 'multitris/comand'
require 'multitris/clientserver'

module Multitris

	# A ClientConcentrator handles connections from multiple
	# Clients. The Game needs only to poll the ClientConcentrator
	# to get the Comands from all Clients. The ClientConcentrator
	# also handles the enumeration of players. If the
	# ClientConcentrator is an observer of a GameBoard, it will
	# tell the client's there color after every change.
	class ClientConcentrator
		
		def initialize
			@clients= [nil]
			@queue= []
		end

		# Adds a new Client to the ClientConcentrator. The
		# ClientConcentrator will take care of receiving from
		# this Client.
		def addClient(io)
			client= ClientServer.new(io) do |cmd|
				@queue<< [client, cmd]
				true
			end
			client.enumerate @clients.size
			@clients[client.number]= client
		end

		# Recieves a Comand from a Client if one is waiting.
		# Returns an array of the following form:
		# [ClientServer, Comand].
		def receive
			@queue.shift
		end

		def update(cmd)
			case cmd
			when :color
				client= @clients[cmd.args[0]]
				if client
					client.transmit(Comand.new(:ATTENTION, cmd.args[1], client.number))
				end
			end
		end

	end

end

