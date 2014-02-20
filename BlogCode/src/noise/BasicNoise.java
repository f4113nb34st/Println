package noise;
import java.util.Random;

/**
 * 
 * Stores methods for generating pseudo-random noise.
 * 
 * @author F4113nb34st
 *
 */
public class BasicNoise implements NoiseFunction
{
	private static final Random rand = new Random();
	
	/**
	 * Generates a consistent, pseudo-random value between 0 and 1 for the given x value.
	 * Generates a consistent, pseudo-random value between 0 and 1 for the given x-y-z values.
	 * @param x The x position..
	 * @param seed The long to seed the noise with.
	 * @return The resulting noise value.
	 */
	public static final double noise_gen(double x, long seed)
	{
		return noise_gen(x, 0, seed);
	}

	/**
	 * Generates a consistent, pseudo-random value between 0 and 1 for the given x-y values.
	 * Generates a consistent, pseudo-random value between 0 and 1 for the given x-y-z values.
	 * @param x The x position.
	 * @param y The y position.
	 * @param seed The long to seed the noise with.
	 * @return The resulting noise value.
	 */
	public static final double noise_gen(double x, double y, long seed)
	{
		return noise_gen(x, y, 0, seed);
	}

	/**
	 * Generates a consistent, pseudo-random value between 0 and 1 for the given x-y-z values.
	 * @param x The x position.
	 * @param y The y position.
	 * @param z The z position.
	 * @param seed The long to seed the noise with.
	 * @return The resulting noise value.
	 */
	public static final double noise_gen(double x, double y, double z, long seed)
	{
		rand.setSeed(seed);
		double xmulti = rand.nextDouble() * 1000;// rand number between 0 and 1000
		double ymulti = rand.nextDouble() * 1000;// rand number between 0 and 1000
		double zmulti = rand.nextDouble() * 1000;// rand number between 0 and 1000
		int n = (int)((x + ((y + (z * zmulti)) * ymulti)) * xmulti);
		n = (n << 13) ^ n;
		return ((1.0 - ((n * (n * n * 15731 + 789221) + 1376312589) & 0x7fffffff) / 1073741824.0) * .5) + .5;
	}
	
	/**
	 * Generates a 2D noise array with the given seed.
	 * @param width The width of the array.
	 * @param height The height of the array.
	 * @param seed The seed to use in the noise generation.
	 * @return The resulting noise array.
	 */
	public static final NoiseArray noise_array(int width, int height, long seed)
	{
		NoiseArray noise = new NoiseArray(width, height);
		fill_noise_array(noise, seed);
		return noise;
	}
	
	/**
	 * Fills the given 2D array with noise of the given seed.
	 * @param noise The array to fill.
	 * @param seed The seed to use in the noise generation..
	 */
	public static final void fill_noise_array(NoiseArray noise, long seed)
	{
		for(int x = noise.minX; x <= noise.maxX; x++)
		{
			for(int y = noise.minY; y <= noise.maxY; y++)
			{
				noise.set(x, y, noise_gen(x, y, seed));
			}
		}
	}
	
	/**
	 * Returns a basic noise function.
	 * @return The noise function.
	 */
	public static final NoiseFunction getAsFunction()
	{
		return new BasicNoise();
	}
	
	private BasicNoise(){}

	@Override
	public void fillArray(NoiseArray array, long seed, int octave)
	{
		fill_noise_array(array, seed);
	}
}
