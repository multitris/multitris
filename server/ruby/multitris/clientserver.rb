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

	class ClientServer < ComandSequence

		attr_reader :name
		attr_reader :number

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

		def enumerate(n)
			@number= n
		end

		def start
			transmit(Comand.new(:goforrest))
		end

		def stop
			transmit(Comand.new(:plonk))
		end

		def win
			transmit(Comand.new(:notbad))
		end

		def lose
			transmit(Comand.new(:thatwasmiserable))
		end

	end

end
