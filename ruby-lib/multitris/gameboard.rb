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
require 'thread'
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
			@color_mutex= Mutex.new
			@next_color= 2
			@color_next_mutex= Mutex.new
			@players= []
			@player_mutex= Mutex.new
			@points= Hash.new(0)
			@points_mutex= Mutex.new
			@width= 0
			@height= 0
			@size_mutex= Mutex.new
			@board= Hash.new
			@pixel_mutex= Mutex.new
			@message_log= []
			@message_mutex= Mutex.new
		end

		# Sets the size of the GUI
		def setSize(width, height)
			@size_mutex.synchronize do
				@width= width
				@height= height
			end
			notify_observers(:size, height, width)
		end

		# Reads the size of the GUI
		def getSize
			@size_mutex.synchronize do
				[@width, @height]
			end
		end

		# Sets the value of one pixel. The meaning of value is
		# is determined by the color map. Pixels can be set to
		# nil.
		def setPixel(x, y, value)
			@pixel_mutex.synchronize do
				return if @board[[x, y]] == value
				if value
					@board[[x, y]]= value
				else
					@board.delete([x, y])
					value= 0
				end
				notify_observers(:set, y, x, value)
			end
		end
		
		# Reads the value of one pixel.
		def getPixel(x, y)
			@pixel_mutex.synchronize do
				@board[[x, y]]
			end
		end

		# Determine wether the pixel at that coordinate is a
		# valid pixel in the GUI.
		def isPixel?(x, y)
			@size_mutex.synchronize do
				x < 0 or y < 0 or x >= @width or y >= @height
			end
		end

		# Resets all pixel. This sets all pixels to nil.
		def resetPixel
			@pixel_mutex.synchronize do
				@board= Hash.new
				notify_observers(:reset, "FIELD")
			end
		end

		# Iterate over all pixel which are not nil. Block will
		# be given three arguments: x, y, value.
		def each_pixel(&block)
			board= nil
			@pixel_mutex.synchronize do
				board= @board.clone
			end
			board.each_key do |key, value|
				block.call(key[0], key[1], value)
			end
		end

		# Tell the GUI to draw all changes since the last
		# flush.
		def flush
			@pixel_mutex.synchronize do
				notify_observers(:flush)
			end
		end

		# Sets a color in the color map. Colors can be set to
		# nil. If no value is given, a random color will be
		# picked.
		def setColor(n, value= :rand)
			if value == :rand
				value= (rand*16777216).floor
			end
			@color_mutex.synchronize do
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
		end

		# Reads a color from the color map.
		def getColor(n)
			@color_mutex.synchronize do
				@colors[n]
			end
		end

		alias :isColor? :getColor

		# Resets all colors in the color map.
		def resetColor
			@color_mutex.synchronize do
				@colors= []
				notify_observers(:reset, "COLOR")
			end
		end

		# Allocates a number for a color. All numbers for non
		# player colors should be allocated here. Only even
		# numbers get allocated, so the ClientConcentrator can
		# use odd numbers for new players. If no value is
		# given, a random color will be picked. Returns the
		# new number.
		def newColor(value= :rand)
			n= nil
			@color_next_mutex.synchronize do
				n= @next_color
				@next_color+= 2
			end
			setColor(n, value)
			n
		end

		# Iterates over all colors in the color map. Block
		# will be given two arguments: n, color
		def each_color(&block)
			colors= nil
			@color_mutex.synchronize do
				colors= @colors.clone
			end
			colors.size.times do |i|
				color= colors[i]
				next unless color
				block.call(i, color)
			end
		end

		# Sets a player name. A player name can be set to nil.
		def setPlayer(n, name)
			@player_mutex.synchronize do
				if name
					@players[n]= name
					notify_observers(:player, n, name)
				else
					@players.delete(n)
					notify_observers(:player, n)
				end
			end
		end

		# Reads a player name.
		def getPlayer(n)
			@player_mutex.synchronize do
				@players[n]
			end
		end

		alias :isPlayer? :getPlayer

		# Resets all player names.
		def resetPlayer
			@player_mutex.synchronize do
				@players= []
				notify_observers(:reset, "PLAYER")
			end
		end

		# Iterates over all player names. Block will be given
		# two arguments: n, name
		def each_player(&block)
			players= nil
			@player_mutex.synchronize do
				players= @players.clone
			end
			players.size.times do |i|
				player= players[i]
				next unless player
				block.call(i, player)
			end
		end

		# Sets points for a player
		def setPoints(n, points)
			@points_mutex.synchronize do
				@points[n]= points
				notify_observers(:points, n, points)
			end
		end

		# Adds points for a player
		def addPoints(n, points)
			setPoints(n, @points[n] + points)
		end

		# Reads points for a player.
		def getPoints(n)
			@points_mutex.synchronize do
				@points[n]
			end
		end

		alias :isPoints? :getPoints

		# Resets all player points.
		def resetPoints
			@points_mutex.synchronize do
				@points= Hash.new(0)
				notify_observers(:reset, "POINTS")
			end
		end

		# Iterates over all players points. Block will be given
		# two arguments: n, points
		def each_points(&block)
			points= nil
			@points_mutex.synchronize do
				points= @points.clone
			end
			points.each do |n, points|
				block.call(n, points)
			end
		end

		# Draws a horizontal line.
		# Write a message to the GUI.
		def message(text)
			@message_mutex.synchronize do
				@message_log << text
				@message_log.shift if @message_log.size > 10
				notify_observers(:message, text)
			end
		end

		# Iterate over the last 10 messages.
		def last_messages(&block)
			message_log= nil
			@message_mutex.synchronize do
				message_log= @message_log.clone
			end
			message_log.each do |msg|
				block.call(msg)
			end
		end

		# Executes a Comand on this gameboard every valid
		# Comand execution should result in the same Comand
		# to be notified to all observers.
		def execute(cmd)
			case cmd.name
			when :size
				self.setSize(cmd.args[1], cmd.args[0])
			when :color
				self.setColor(cmd.args[0], cmd.args[1])
			when :set
				color= cmd.args[2]
				color= nil if color == 0
				self.setPixel(cmd.args[1], cmd.args[0], color)
			when :flush
				self.flush
			when :player
				self.setPlayer(cmd.args[0], cmd.args[1])
			when :points
				self.setPoints(cmd.args[0], cmd.args[1])
			when :reset
				case cmd.args[0]
				when :color
					self.resetColor
				when :field
					self.resetPixel
				when :player
					self.resetPlayer
				when :points
					self.resetPoints
				end
			when :message
				self.message(cmd.args[0])
			end
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

