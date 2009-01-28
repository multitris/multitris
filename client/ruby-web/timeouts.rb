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

# A class that let's you manage timeouts.
class Timeouts

	# Time specifies how long the timeouts will be.
	def initialize(time)
		@time= 1.0 * time; # we need a float
		@blocks= Hash.new
		@sleeps= []
		@times= Hash.new
		@mutex= Hash.new
		Thread.new do
			while true
				if @sleeps[0]
					time= @sleeps[0][0]
					name= @sleeps[0][1]
					wait= time-Time.now
					if wait > 0
						sleep wait # wait for the timeout
					else
						@mutex[name].synchronize do
							if @times[name] and (@times[name] < Time.now)
								block= @blocks[name] # ensure no other thread deleted it before calling
								block.call if block
								@times.delete(name)
								@blocks.delete(name)
							end
							@sleeps.shift
						end
					end
				else
					sleep @time/10
				end
			end
		end
	end

	# Adds a timeout, the block will be yield when the timeout
	# arrives. Be careful the block will be yield in another
	# Thread. Also be careful the execution of the block, blocks
	# the execution of other timeouts.
	def register(name, &block)
		@mutex[name]= Mutex.new
		@blocks[name]= block
		reset(name)
	end

	# Resets a timeout. This needs to be called fairly regular,
	# or the timeout will arrive.
	def reset(name)
		time= Time.now + @time
		@mutex[name].synchronize do
			@times[name]= time
			@sleeps << [time, name]
		end
	end

	# Removes a timeout
	def clear(name)
		@mutex[name].synchronize do
			@times.delete(name)
			@blocks.delete(name)
		end
	end

end
