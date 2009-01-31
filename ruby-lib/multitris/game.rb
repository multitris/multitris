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

		# The arguments for the Game as an array of Symbols.
		# If the Symbols are in an additional array, these are
		# optional arguments.
		def self.args_names
			[]
		end

		# The classes for the Games arguments. Stored in the
		# same way as the names in self.args_names. These
		# classes are used to validate the arguments and to
		# convert them to the wanted classes. Possible values
		# are String, Int, and string object. If a string
		# object is given, it must equal the string given as
		# an argument.
		def self.args_classes
			[]
		end

		# Format the self.args_names in a readable way.
		def self.args_format(args= args_names)
			args.collect do |arg|
				case arg
				when Symbol
					arg.to_s.upcase
				when Array
					"["+args_format(arg)+"]"
				end
			end.join(" ")
		end

		# Validate wether the content of args matches
		# self.args_classes . args must be an Array of
		# Strings.
		def self.args_validate(args, classes= nil)
			final= !classes
			classes= args_classes unless classes
			args= args.clone
			classes.each do |cl|
				return false if args.size == 0 and !(Array === cl)
				case cl
				when String
					if cl == args[0]
						args.shift
					else
						return false
					end
				when Array
					if size= args_validate(args, cl)	
						args= args[args.size-size, args.size-1]
					end
				else
					if ((cl == Integer) and (args[0]=~ /^\d+$/)) or (cl == String)
						args.shift
					else
						return false
					end
				end
			end
			if final
				args.size == 0
			else
				args.size
			end
		end
			

		def initialize(board, clients)
			@board= board
			@clients= clients
		end

	end

end
