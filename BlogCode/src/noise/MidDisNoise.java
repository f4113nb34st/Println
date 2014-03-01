package noise;

/**
 * 
 * Class that generates Midpoint Displacement noise.
 * 
 * @author F4113nb34st
 *
 */
public final class MidDisNoise extends SeededNoise
{
	/**
	 * The sentinel value for seeded noise.
	 */
	public static final double SENTINEL = Double.POSITIVE_INFINITY;
	/**
	 * The NoiseGenerator instance for this MidpointDisplacementNoise.
	 */
	private NoiseGenerator gen = new NoiseGenerator();
	/**
	 * The starting amplitude of this noise.
	 */
	public double startingAmplitude;
	/**
	 * The persistance of the iterations.
	 */
	public double persistance; 
	/**
	 * True if working with seeded noise.
	 */
	public boolean seeded;
	
	/**
	 * Creates a new MidpointDisplacementNoise with the given seed, starting Amp, persistence, and seeded.
	 * @param seed The seed.
	 * @param amp The starting amplitude.
	 * @param persis The persistence.
	 * @param seded Whether or not we are working with seeded noise.
	 */
	public MidDisNoise(long seed, double amp, double persis, boolean seded)
	{
		super(seed);
		startingAmplitude = amp;
		persistance = persis;
		seeded = seded;
	}

	@Override
	public void fillArray(NoiseArray noise)
	{
		//only set the 
		if(!seeded || noise.get(0, 0) == SENTINEL)
		{
			noise.set(0, 0, .5 + ((gen.noise_gen(seed, 0, 0) - .5) * 2 * startingAmplitude));
		}
		calculate(noise);
	}
	
	/**
	 * Clips the size to the form (2^i).
	 * @param size The previous size.
	 * @return The clipped size.
	 */
	private int fixSize(int size)
	{
		int actingSize = 2;
		while(actingSize < size)
		{
			actingSize *= 2;
		}
		return actingSize;
	}
	
	private void calculate(NoiseArray noise)
	{
		//get whatever value of 2^i is greater than or equal to the max dimension of the noise
		int totalSize = fixSize(Math.max(noise.getWidth(), noise.getHeight()));
		
		int halfSize = totalSize / 2;
		double amplitude = startingAmplitude * persistance;
		while(halfSize > 0)
		{
			//do square step
			for(int x = halfSize; x < totalSize; x += (halfSize * 2))
			{
				for(int y = halfSize; y < totalSize; y += (halfSize * 2))
				{
					squareStep(noise, x, y, halfSize, amplitude);
				}
			}
			//do diamond steps
			for(int x = halfSize; x < totalSize; x += (halfSize * 2))
			{
				for(int y = halfSize; y < totalSize; y += (halfSize * 2))
				{
					diamondStep(noise, x - halfSize, y, halfSize, amplitude);
					diamondStep(noise, x, y - halfSize, halfSize, amplitude);
				}
			}
			halfSize /= 2;
			amplitude *= persistance;
		}
	}
	
	private void squareStep(NoiseArray noise, int x, int y, int halfSize, double amplitude)
	{
		//skip pre-seeded values
		if(seeded && noise.get(x, y) != SENTINEL)
		{
			return;
		}
		
		double value = 0;
		//no need to wrap, NoiseArray handles that for us
		value += noise.get(x - halfSize, y - halfSize);
		value += noise.get(x + halfSize, y - halfSize);
		value += noise.get(x - halfSize, y + halfSize);
		value += noise.get(x + halfSize, y + halfSize);
		value /= 4;
		
		noise.set(x, y, value + ((gen.noise_gen(seed, x, y) - .5) * 2 * amplitude));
	}
	
	private void diamondStep(NoiseArray noise, int x, int y, int halfSize, double amplitude)
	{
		//skip pre-seeded values
		if(seeded && noise.get(x, y) != SENTINEL)
		{
			return;
		}
		
		double value = 0;
		//no need to wrap, NoiseArray handles that for us
		value += noise.get(x - halfSize, y);
		value += noise.get(x + halfSize, y);
		value += noise.get(x, y - halfSize);
		value += noise.get(x, y + halfSize);
		value /= 4;
		
		noise.set(x, y, value + ((gen.noise_gen(seed, x, y) - .5) * 2 * amplitude));
	}
}
