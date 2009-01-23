
# A class that let's you manage timeouts.
class Timeouts

	# Time specifies how long the timeouts will be.
	def initialize(time)
		@time= 1.0 * time; # we need a float
		@blocks= Hash.new
		@sleeps= []
		@times= Hash.new
		Thread.new do
			while true
				if @sleeps[0]
					time= @sleeps[0][0]
					name= @sleeps[0][1]
					wait= time-Time.now
					if wait > 0
						sleep wait # wait for the timeout
					else
						if @times[name] and (@times[name] < Time.now)
							block= @blocks[name] # ensure no other thread deleted it before calling
							block.call if block
						end
						@sleeps.shift
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
		@blocks[name]= block
		reset(name)
	end

	# Resets a timeout. This needs to be called fairly regular,
	# or the timeout will arrive.
	def reset(name)
		time= Time.now + @time
		@times[name]= time
		@sleeps << [time, name]
	end

	# Removes a timeout
	def clear(name)
		@times.delete(name)
		@blocks.delete(name)
	end

end
