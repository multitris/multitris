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

module Multitris

	# The parent class of all Games. The game is given a GameBoard
	# and a ClientConcentrator. The GameBoard's purpose is to
	# output to the GUI's. The purpose of the ClientConcentrator
	# is to get the Comands from all Clients.
	class Game

		def initialize(board, clients)
			@board= board
			@clients= clients
		end

	end

end
