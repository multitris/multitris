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

	# This Class handles multiple GUI connections and feeds them
	# from a GameBoard. GUI's can be added after the game started
	# as the GUIConcentrator repeats the missing Comands for the
	# new GUI's
	class GUIConcentrator
	
		# board is the Board all GUI connections will be feed
		# from.
		def initialize(board)
			@board= board
			@guis= []
		end

		# Adds a GUI connection to the GameBoard. The new gui
		# connection must be a child class of IO.
		def addGUI(io)
			gui= ComandSequence.new(io)
			gui.add_observer(self)
			width, height= @board.getSize
			gui.transmit(Comand.new(:size, height, width))
			@board.each_color do |n, color|
				gui.transmit(Comand.new(:color, n, color))
			end
			@board.each_player do |n, player|
				gui.transmit(Comand.new(:player, n, player))
			end
			@board.each_points do |n, points|
				gui.transmit(Comand.new(:points, n, points))
			end
			@board.each_pixel do |x, y, value|
				gui.transmit(Comand.new(:set, y, x, value))
			end
			gui.transmit(Comand.new(:flush))
			@board.add_observer(gui)
			@guis << gui
		end

		# Remove a ComandSequence from the GUIConcentrator.
		def rmGUI(gui)
			return unless @guis.include?(gui)
			@board.delete_observer(gui)
			@guis.delete(gui)
		end

		# When this method is called with a ComandSequence and
		# msg==:close, the ComandSequence will be removed from
		# the GUIConecentrator.
		def update(gui, msg)
			if ComandSequence === gui and msg == :close
				rmGUI(gui)
			end
		end

	end

end

