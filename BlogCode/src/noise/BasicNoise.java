package noise;

/**
 * 
 * Stores methods for generating pseudo-random noise.
 * 
 * @author F4113nb34st
 *
 */
public final class BasicNoise extends SeededNoise
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
}
