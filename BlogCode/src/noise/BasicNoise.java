package noise;

import util.concurrent.ArrayTask;
import util.concurrent.ThreadPool;

/**
 * 
 * Stores methods for generating pseudo-random noise.
 * 
 * @author F4113nb34st
 *
 */
public final class BasicNoise extends SeededNoise implements MultiThreadedNoise
{
	/**
	 * The NoiseGenerator instance for this BasicNoise.
	 */
	private NoiseGenerator gen = new NoiseGenerator();
	
	/**
	 * Creates a new BasicNoise with the given seed.
	 * @param s
	 */
	public BasicNoise(long s)
	{
		super(s);
	}

	@Override
	public void fillArray(NoiseArray noise)
	{
		//for all pixels
		for(int x = 0; x <= noise.getWidth(); x++)
		{
			for(int y = 0; y <= noise.getHeight(); y++)
			{
				//get to noise value
				noise.setRelative(x, y, gen.noise_gen(seed, x, y));
			}
		}
	}
	
	public void fillMultiThreaded(NoiseArray noise, ThreadPool pool)
	{
		//add the task all threads will run
		pool.addGlobalTask(new ColumnTask(noise, 0, noise.getWidth() - 1));
		//start pool and wait for completion
		pool.startAndWait();
	}
	
	/**
	 * Fills columns with basic noise until all have been filled.
	 */
	private class ColumnTask extends ArrayTask
	{
		/**
		 * NoiseArray to fill.
		 */
		private final NoiseArray noise;
		
		private ColumnTask(NoiseArray array, int min, int max)
		{
			super(min, max);
			noise = array;
		}

		@Override
		public void run(int x)
		{
			//for all y's in column
			for(int y = 0; y <= noise.getHeight(); y++)
			{
				//set to noise value
				noise.setRelative(x, y, gen.noise_gen(seed, x, y));
			}
		}
	}
}
