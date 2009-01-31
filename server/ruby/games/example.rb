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

require 'multitris/game'

module Games

	class Example < Multitris::Game
		
		def initialize(board, clients)
			super(board, clients)
			@board= board
			@xy= []
			Thread.new do
				board.setSize(64, 64)
				loop do
					while a= clients.receive
						client, cmd= a
						case cmd
						when :new
							board.setColor(client.number)
							board.setPlayer(client.number, client.name)
							randPosition(client)
							client.start
						when :leave
							board.setPlayer(client.number, nil)
							board.setPixel(@xy[client.number][0], @xy[client.number][1], nil)
							board.flush
							@xy.delete(client.number)
						when :left
							move(client) do |xy|
								[xy[0]-1, xy[1]]
							end
						when :right
							move(client) do |xy|
								[xy[0]+1, xy[1]]
							end
						when :up
							move(client) do |xy|
								[xy[0], xy[1]-1]
							end
						when :down
							move(client) do |xy|
								[xy[0], xy[1]+1]
							end
						when :button
							randPosition(client)
						end
					end
				end
			end
		end

		def randPosition(client)
			xy= [(rand*64).floor, (rand*64).floor]
			while @board.getPixel(*xy)
				xy= [(rand*64).floor, (rand*64).floor]
			end
			@board.setPixel(*(xy +[client.number]))
			@board.flush
			@xy[client.number]= xy
		end

		def move(client, &block)
			xy= @xy[client.number]
			new= block.call(xy)
			unless @board.isPixel?(*new) or @board.getPixel(*new)
				@board.setPixel(*(xy +[nil]))
				@board.setPixel(*(new +[client.number]))
				@board.flush
				@xy[client.number]= new
			end
		end

	end

end
