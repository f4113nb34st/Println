package noise;

/**
 * 
 * Superclass of all noise that is seeded.
 * 
 * @author F4113nb34st
 *
 */
public abstract class SeededNoise extends Noise
{
	/**
	 * The seed of this noise.
	 */
	public long seed;
	
	/**
	 * Creates a new SeededNoise with the given seed.
	 * @param s The seed.
	 */
	public SeededNoise(long s)
	{
		seed = s;
	}
}
