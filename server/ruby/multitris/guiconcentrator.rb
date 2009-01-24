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

module Multitris

	class GUIConcentrator
		
		def initialize(board)
			@board= board
			@guis= []
		end

		def addGUI(io)
			gui= ComandSequence.new(io)
			@board.add_observer(gui)
			width, height= @board.getSize
			gui.transmit(Comand.new(:size, height, width))
			@board.each_color do |n, color|
				gui.transmit(Comand.new(:color, n, color))
			end
			@board.each_player do |n, player|
				gui.transmit(Comand.new(:player, n, player))
			end
			@board.each_pixel do |x, y, value|
				gui.transmit(Comand.new(:set, y, x, value))
			end
			gui.transmit(Comand.new(:flush))
			@guis << gui
		end

	end

end

