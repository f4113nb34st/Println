package noise;

import util.concurrent.ArrayTask;
import util.concurrent.ThreadPool;

/**
 * 
 * Stores methods for generating Fractal noise.
 * 
 * @author F4113nb34st
 *
 */
public final class FractalNoise extends SeededNoise implements MultiThreadedNoise
{
	/**
	 * The NoiseGenerator instance for this FractalNoise.
	 */
	private NoiseGenerator gen = new NoiseGenerator();
	/**
	 * The seed of this noise.
	 */
	public long seed;
	/**
	 * The noise function to use to generate the octaves.
	 */
	public PeriodicNoise baseNoise;
	/**
	 * The finest octave.
	 */
	public int fineOctave;
	/**
	 * The broadest octave.
	 */
	public int broadOctave;
	/**
	 * The persistence of the octaves.
	 */
	public double persistence;
	
	/**
	 * Creates a new FractalNoise with the given seed, base noise, fine and broad octaves, and persistence.
	 * @param s The seed.
	 * @param base The base noise generator.
	 * @param fine The fine octave.
	 * @param broad The broad octave.
	 * @param persis The persistence.
	 */
	public FractalNoise(long s, PeriodicNoise base, int fine, int broad, double persis)
	{
		super(s);
		baseNoise = base;
		fineOctave = fine;
		broadOctave = broad;
		persistence = persis;
	}

	@Override
	public void fillArray(NoiseArray noise)
	{
		//create array of octaves
		NoiseArray[] octaves = new NoiseArray[broadOctave + 1 - fineOctave];
		
		//starting at top octave and going down
		for(int octave = broadOctave; octave >= fineOctave; octave--)
		{
			//create new array for octave
			octaves[octave - fineOctave] = new NoiseArray(noise.getWidth(), noise.getHeight());
			//generate the octave seed
			long octaveSeed = (long)(Long.MAX_VALUE * gen.noise_gen(seed, (double)octave));
			//fill from noise function with a random seed
			baseNoise.fillArray(octaves[octave - fineOctave], octaveSeed, octave);
		}
		
		//for all pixels
		for(int x = 0; x < noise.getWidth(); x++)
		{
			for(int y = 0; y < noise.getHeight(); y++)
			{
				//init value
				double value = 0;
				//initial amplitude
				double currentAmp = 1;
				//amplitude so far
				double totalAmp = 0;
				//stating at top octave and going down
				for(int octave = broadOctave; octave >= fineOctave; octave--)
				{
					//increment value by octave value * currentAmp
					value += octaves[octave - fineOctave].get(x, y) * currentAmp;
					//increase total amp
					totalAmp += currentAmp;
					//modify current amp
					currentAmp *= persistence;
				}
				//normalizes the array
				value /= totalAmp;
				
				//set value
				noise.setRelative(x, y, value);
			}
		}
	}
	
	public void fillMultiThreaded(NoiseArray noise, ThreadPool pool)
	{
		//create array of octaves
		NoiseArray[] octaves = new NoiseArray[broadOctave + 1 - fineOctave];
		
		//starting at top octave and going down
		for(int octave = broadOctave; octave >= fineOctave; octave--)
		{
			//create new array for octave
			octaves[octave - fineOctave] = new NoiseArray(noise.getWidth(), noise.getHeight());
			//generate the octave seed
			long octaveSeed = (long)(Long.MAX_VALUE * gen.noise_gen(seed, (double)octave));
			//fill from noise function with a random seed
			addBaseNoiseFillTask(pool, noise, octaves, octave, octaveSeed);
		}
		pool.startAndWait();
		
		pool.addGlobalTask(new ColumnTask(noise, octaves, 0, noise.getWidth() - 1));
		pool.startAndWait();
	}
	
	private void addBaseNoiseFillTask(ThreadPool pool, final NoiseArray noise, final NoiseArray[] octaves, final int octave, final long octaveSeed)
	{
		pool.addTask(new Runnable()
		{
			@Override
			public void run()
			{
				
				baseNoise.copy().fillArray(octaves[octave - fineOctave], octaveSeed, octave);
			}
		});
	}
	
	/**
	 * Task that calculates fractal noise columns.
	 */
	private class ColumnTask extends ArrayTask
	{
		//the noise array
		private final NoiseArray noise;
		//the pre-calced octaves
		private final NoiseArray[] octaves;
		
		private ColumnTask(NoiseArray array, NoiseArray[] octs, int min, int max)
		{
			super(min, max);
			noise = array;
			octaves = octs;
		}

		@Override
		public void run(int x)
		{
			//for all y's in column
			for(int y = 0; y < noise.getHeight(); y++)
			{
				//init value
				double value = 0;
				//initial amplitude
				double currentAmp = 1;
				//amplitude so far
				double totalAmp = 0;
				//stating at top octave and going down
				for(int octave = broadOctave; octave >= fineOctave; octave--)
				{
					//increment value by octave value * currentAmp
					value += octaves[octave - fineOctave].get(x, y) * currentAmp;
					//increase total amp
					totalAmp += currentAmp;
					//modify current amp
					currentAmp *= persistence;
				}
				//normalizes the array
				value /= totalAmp;
				
				//set value
				noise.setRelative(x, y, value);
			}
		}
	}
}
