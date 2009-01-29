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

	class Maze < Multitris::Game

		Width= 32
		Height= 32
		
		def initialize(board, clients)
			super(board, clients)
			@board= board
			@clientes= clients
			@positions= Hash.new
			@color_wall= @board.newColor(nil)
			Thread.new do
				i= 0
				loop do
					f= false
					@positions.each do |key, a|
						a= a.clone
						next if a.size < 2
						f= true
						@board.setPixel(key[0], key[1], a[i % a.size])
					end
					@board.flush if f
					i+= 1
					sleep 0.2
				end
			end
			Thread.new do
				@board.setSize(Width, Height)
				newGame
				loop do
					while a= clients.receive
						client, cmd= a
						case cmd
						when :new
							board.setColor(client.number)
							board.setPlayer(client.number, client.name)
							setStart(client)
							client.start
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
							setStart(client)
						end
						xy= @xy[client.number]
						if xy[0] == 0 or xy[1] == 0
							@board.addPoints(client.number, 1)
							@board.message("It took #{client.name} #{@steps[client.number]} step to escape")
							newGame
						end
					end
					sleep 0.01
				end
			end
		end

		def generateMaze
			@board.resetPixel
			@board.drawHLine(2, Width-1, 0, @color_wall)
			@board.drawHLine(0, Width-1, Height-1, @color_wall)
			@board.drawVLine(0, 2, Height-1, @color_wall)
			@board.drawVLine(Width-1, 0, Height, @color_wall)
			taken= Hash.new
			Height.times { |i| taken[[0, i]]= true }
			Height.times { |i| taken[[Width-1, i]]= true }
			Width.times { |i| taken[[i, 0]]= true }
			Width.times { |i| taken[[i, Height-1]]= true }
			mazeGo(1, 1, taken, 1) { |x, y| [1, 1] }
			Width.times do |x|
				Height.times do |y|
					next if taken[[x, y]]
					@board.setPixel(x, y, @color_wall)
				end
			end
		end

		def mazeGo(x, y, taken, length, &block)
			x,y= block.call(x, y)
			return if taken[[x, y]]
			taken[[x, y]]= :way
			a= []
			mazeTest(x, y-1, taken)
			mazeTest(x, y+1, taken)
			mazeTest(x-1, y, taken)
			mazeTest(x+1, y, taken)
			@length[[x, y]]= length
			a<< lambda { |x, y| [x, y-1] }
			a<< lambda { |x, y| [x, y+1] }
			a<< lambda { |x, y| [x-1, y] }
			a<< lambda { |x, y| [x+1, y] }
			a= a.sort { |a, b| (rand*3).floor-1 }
			while b= a.pop
				if (rand*4).floor != 0
					mazeGo(x, y, taken, length+1, &b)
				end
			end
		end

		def mazeTest(x, y, taken)
			return if taken[[x, y]]
			i= -1
			i+= 1 if taken[[x, y-1]]==:way
			i+= 1 if taken[[x, y+1]]==:way
			i+= 1 if taken[[x-1, y]]==:way
			i+= 1 if taken[[x+1, y]]==:way
			if i > 0
				taken[[x, y]]= true
				@board.setPixel(x, y, @color_wall)
			end
		end

		def setStart(client, flush= true)
			removePlayer(client)
			setPlayer(@start[0],@start[1], client)
			@board.flush if flush
		end

		def move(client, &block)
			xy= @xy[client.number]
			new= block.call(xy)
			return if @board.getPixel(*new)==@color_wall
			@steps[client.number]+= 1
			removePlayer(client)
			setPlayer(new[0], new[1], client)
			@board.flush
		end

		def setPlayer(x, y, client)
			xy= [x, y]
			@xy[client.number]= xy
			if @positions[xy]
				@positions[xy] << client.number
			else
				@positions[xy]= [client.number]
				@board.setPixel(xy[0], xy[1], client.number)
			end
		end

		def removePlayer(client)
			xy= @xy[client.number]
			if xy
				@positions[xy].delete(client.number)
				case @positions[xy].size
				when 0
					@positions.delete(xy)
					@board.setPixel(xy[0], xy[1], nil)
				when 1 # make sure the right color is there if not blinking anymore
					@board.setPixel(xy[0], xy[1], @positions[xy])
				end
			end
		end

		def newGame
			@length= Hash.new
			@steps= Hash.new(0)
			@board.setColor(@color_wall)
			generateMaze
			@start= [((Width-2)*rand).floor+1, ((Height-2)*rand).floor+1]
			while @board.getPixel(@start[0], @start[1])
				@start= [((Width-2)*rand).floor+1, ((Height-2)*rand).floor+1]
			end
			@board.message("the shortest path takes #{@length[@start]} steps")
			@board.message("be the first to escape the MAZE")
			@xy= []
			@positions= Hash.new
			@clients.each do |player|
				setStart(player, false)
			end
			@board.flush
		end

	end

end
