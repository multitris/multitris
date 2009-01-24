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

require 'observer'
require 'multitris/comand'

module Multitris

	# A GameBoard represents the GUI. The GameBoards implements
	# the observer pattern to tell all GUI's of changes. The
	# observers will be given a Comand. The GameBoard buffers all
	# data for later retrieval.
	class GameBoard
		include Observable

		# Construct a Comand from args and notify all
		# observers with this Comand.
		def notify_observers(*args)
			changed
			super(Comand.new(args.shift, args))
		end
		
		def initialize
			@colors= []
			@players= []
			@points= Hash.new(0)
			@width= 0
			@height= 0
			@board= Hash.new
		end

		# Sets the size of the GUI
		def setSize(width, height)
			@width= width
			@height= height
			notify_observers(:size, height, width)
		end

		# Reads the size of the GUI
		def getSize
			[@width, @height]
		end

		# Sets the value of one pixel. The meaning of value is
		# is determined by the color map. Pixels can be set to
		# nil.
		def setPixel(x, y, value)
			return if @board[[x, y]] == value
			if value
				@board[[x, y]]= value
			else
				@board.delete([x, y])
				value= 0
			end
			notify_observers(:set, y, x, value)
		end
		
		# Reads the value of one pixel.
		def getPixel(x, y)
			@board[[x, y]]
		end

		# Determine wether the pixel at that coordinate is a
		# valid pixel in the GUI.
		def isPixel?(x, y)
			x < 0 or y < 0 or x >= @width or y >= @height
		end

		# Resets all pixel. This sets all pixels to nil.
		def resetPixel
			@board= Hash.new
			notify_observers(:reset, "FIELD")
		end

		# Iterate over all pixel which are not nil. Block will
		# be given three arguments: x, y, value.
		def each_pixel(&block)
			@board.each_key do |key|
				block.call(*(key + [@board[key]]))
			end
		end

		# Tell the GUI to draw all changes since the last
		# flush.
		def flush
			notify_observers(:flush)
		end

		# Sets a color in the color map. Colors can be set to
		# nil.
		def setColor(n, value= :rand)
			if value == :rand
				value= (rand*16777216).floor
			end
			if value
				@colors[n]= value
			else
				@colors.delete(n)
				value= 0
			end
			if Integer===value
				value= value.to_s(16)
				value= "0"*(6-value.size) + value
			end
			notify_observers(:color, n, value)
		end

		# Reads a color from the color map.
		def getColor(n)
			@colors[n]
		end

		alias :isColor? :getColor

		# Resets all colors in the color map.
		def resetColor
			@colors= []
			notify_observers(:reset, :color)
		end

		# Iterates over all colors in the color map. Block
		# will be given two arguments: n, color
		def each_color(&block)
			@colors.size.times do |i|
				color= @colors[i]
				next unless color
				block.call(i, color)
			end
		end

		# Sets a player name. A player name can be set to nil.
		def setPlayer(n, name)
			if name
				@players[n]= name
				notify_observers(:player, n, name)
			else
				@players.delete(n)
				notify_observers(:player, n)
			end
		end

		# Reads a player name.
		def getPlayer(n)
			@players[n]
		end

		alias :isPlayer? :getPlayer

		# Resets all player names.
		def resetPlayer
			@players= []
			notify_observers(:reset, :player)
		end

		# Iterates over all player names. Block will be given
		# two arguments: n, name
		def each_player(&block)
			@players.size.times do |i|
				player= @players[i]
				next unless player
				block.call(i, player)
			end
		end

		# Sets points for a player
		def setPoints(n, points)
			@points[n]= points
			notify_observers(:points, n, points)
		end

		# Adds points for a player
		def addPoints(n, points)
			setPoints(n, @points[n] + points)
		end

		# Reads points for a player.
		def getPoints(n)
			@points[n]
		end

		alias :isPoints? :getPoints

		# Resets all player points.
		def resetPoints
			@points= Hash.new(0)
			notify_observers(:reset, :points)
		end

		# Iterates over all players points. Block will be given
		# two arguments: n, points
		def each_points(&block)
			@points.each do |n, points|
				block.call(n, points)
			end
		end

		# Draws a horizontal line.
		# Write a message to the GUI.
		def message(text)
			notify_observers(:message, text)
		end

		# Draws a horizontal line.
		def drawHLine(x1, x2, y, value)
			x1.upto(x2) do |x|
				setPixel(x, y, value)
			end
		end

		# Draws a vertical line.
		def drawVLine(x, y1, y2, value)
			y1.upto(y2) do |y|
				setPixel(x, y, value)
			end
		end

	end

end

