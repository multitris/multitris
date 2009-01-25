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

class Symbol

	alias :bmultitriscomand :===
	private :bmultitriscomand
	def ===(other)
		if Multitris::Comand===other
			other.name==self
		else
			bmultitriscomand(other)
		end
	end

end

module Multitris

	# This class represents a Multitris Comand. Multitris Comands
	# are transmitted over ComandSequences. Every Comand
	# has a name and optional arguments.
	class Comand

		# The name of the Comand
		attr_accessor :name

		# The optional arguments of the Comand
		attr_accessor :args

		def initialize(name, *args)
			@name= name
			@args= args.flatten
		end

		# Create a Comand from a string. The string is in the
		# format as transmitted over a ComandSequence. This
		# is "NAME[ arg1[ arg2[...]]]".
		def self.from_string(str)
			result= Comand.new(nil)
			result.args= str.split(" ")
			result.name= result.args.shift.downcase.to_sym
			result
		end

		# Format a Comand as transmitted over a ComandSequence.
		# This is "NAME[ arg1[ arg2[...]]]".
		def to_s
			result= ([self.name.to_s.upcase] + self.args).join(" ")
			result
		end

		# This method is the same as comand.name == sym
		def ===(sym)
			self.name === sym
		end

	end

end
