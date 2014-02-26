package noise;

import util.FinalRandom;

/**
 * 
 * Simple class that generates noise values from seeds and coords.
 * 
 * @author F4113nb34st
 *
 */
public class NoiseGenerator
{
	//random instance for rand values generation
	private final FinalRandom rand = new FinalRandom();
	
	/**
	 * Generates a consistent, pseudo-random value between 0 and 1 for the given coords.
	 * @param seed The long to seed the noise with.
	 * @param coords The coords of the noise point.
	 * @return The resulting noise value.
	 */
	public double noise_gen(long seed, double... coords)
	{
		rand.setSeed(seed);
		double storage = 0;
		for(double coord : coords)
		{
			storage += coord;
			storage *= (27 * rand.nextDouble()) + 31;
		}
		//mangle inputs beyond recognition
		int n = (int)storage;
		n = (n << 13) ^ n;
		return ((1.0 - ((n * (n * n * 15731 + 789221) + 1376312589) & 0x7fffffff) / 1073741824.0) * .5) + .5;
	}
}
