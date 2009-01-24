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

require 'multitris/comand'

module Multitris

	# A ClientServer handles the connection to one Client.
	class ClientServer < ComandSequence

		# The players name.
		attr_reader :name

		# The number of the player.
		attr_reader :number

		# The argument must be a child class of IO. If given a
		# block, the block will be yield for every Comand
		# received. Comands names are translated into easier
		# to understand names. The possible values are:
		# - :new
		# - :left
		# - :right
		# - :up
		# - :down
		# - :button
		def initialize(io)
			super(io) do |cmd|
				case cmd
				when :iwantfun
					@name= cmd.args[1]
					cmd.name= :new
				when :lafontaine
					cmd.name= :left
				when :stoiber
					cmd.name= :right
				when :moellemann
					cmd.name= :down
				when :marihuana
					cmd.name= :up
				when :turn
					cmd.name= :button
				end
				if block_given?
					yield(cmd)
				end
				false
			end
		end

		# Sets the player number.
		def enumerate(n)
			@number= n
		end

		# Transmits a start Comand.
		def start
			transmit(Comand.new(:goforrest))
		end

		# Transmits a  stop Comand.
		def stop
			transmit(Comand.new(:plonk))
		end

		# Transmits a 'you win' Comand.
		def win
			transmit(Comand.new(:notbad))
		end

		# Transmits a 'you lose' Comand.
		def lose
			transmit(Comand.new(:thatwasmiserable))
		end

	end

end
