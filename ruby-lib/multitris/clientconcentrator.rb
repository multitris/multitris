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

require 'thread'
require 'multitris/comandsequence'
require 'multitris/comand'
require 'multitris/clientserver'

module Multitris

	# A ClientConcentrator handles connections from multiple
	# Clients. The Game needs only to poll the ClientConcentrator
	# to get the Comands from all Clients. The ClientConcentrator
	# also handles the enumeration of players. Players only get
	# odd numbers, so the GameBoard can use even numbers for non
	# player colors. If the ClientConcentrator is an observer of a
	# GameBoard, it will tell the client's there color after every
	# change.
	class ClientConcentrator
		
		def initialize
			@clients= []
			@clients_next= 1
			@clients_mutex= Mutex.new
			@queue= []
			@queue_mutex= Mutex.new
		end

		# Adds a new Client to the ClientConcentrator. The
		# ClientConcentrator will take care of receiving from
		# this Client.
		def addClient(io)
			client= ClientServer.new(io) do |cmd|
				@queue_mutex.synchronize do
					@queue<< [client, cmd]
				end
				true
			end
			@clients_mutex.synchronize do
				client.enumerate @clients_next
				@clients_next+= 2
				@clients[client.number]= client
			end
			client.add_observer(self)
		end

		# Removes a ClientServer from the ClientConcentrator.
		def rmClient(client)
			@queue_mutex.synchronize do
				@queue<< [client, Comand.new(:leave)]
			end
			@clients_mutex.synchronize do
				@clients.delete(client)
			end
		end

		# Recieves a Comand from a Client if one is waiting.
		# Returns an array of the following form:
		# [ClientServer, Comand].
		def receive
			@queue_mutex.synchronize do
				@queue.shift
			end
		end

		# Iterates over all clients
		def each(&block)
			@clients_mutex.synchronize do
				clients= @clients.clone
			end
			@clients.each do |client|
				next unless client
				block.call(client)
			end
		end

		# ClientConcentrator can receive updates from
		# Gameboard's and ComandSequence's. On an update from
		# a Gameboard, the ClientConcentrator notifies all
		# clients if there color changes. On an exit update
		# from a ComandSequence, the ClientConcentrator
		# notifies the Game and and deletes ComandSequence.
		def update(*args)
			if args.size == 1 and Comand === args[0]
				# update from GameBoard
				case cmd
				when :color
					client= @clients[cmd.args[0]]
					if client
						client.transmit(Comand.new(:ATTENTION, cmd.args[1], client.number))
					end
				end
			elsif args.size == 2 and ComandSequence === args[0]
				# update from ComandSequence
				if args[1] == :close
					rmClient(args[0])
				end
			end
		end

	end

end

