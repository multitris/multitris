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

	class GameBoard
		include Observable

		def notify_observers(*args)
			changed
			super(Comand.new(args.shift, args))
		end
		
		def initialize
			@colors= []
			@players= []
			@width= 0
			@height= 0
			@board= Hash.new
		end

		def setSize(width, height)
			@width= width
			@height= height
			notify_observers(:size, height, width)
		end

		def getSize
			[@width, @height]
		end
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
		
		def getPixel(x, y)
			@board[[x, y]]
		end

		def isPixel?(x, y)
			x < 0 or y < 0 or x >= @width or y >= @height
		end

		def resetPixel
			@board= Hash.new
			notify_observers(:reset, :pixel)
		end

		def each_pixel(&block)
			@board.each_key do |key|
				block.call(*(key + [@board[key]]))
			end
		end

		def flush
			notify_observers(:flush)
		end

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

		def getColor(n)
			@colors[n]
		end

		alias :isColor? :getColor

		def resetColor
			@colors= []
			notify_observers(:reset, :color)
		end

		def each_color(&block)
			@colors.size.times do |i|
				color= @colors[i]
				next unless color
				block.call(i, color)
			end
		end

		def setPlayer(n, name)
			if name
				@players[n]= name
				notify_observers(:player, n, name)
			else
				@players.delete(n)
				notify_observers(:player, n)
			end
		end

		def getPlayer(n)
			@players[n]
		end

		alias :isPlayer? :getPlayer

		def resetPlayer
			@players= []
			notify_observers(:reset, :player)
		end

		def each_player(&block)
			@players.size.times do |i|
				player= @players[i]
				next unless player
				block.call(i, player)
			end
		end

		def message(text)
			notify_observers(:message, text)
		end

	end

end

