package noise;

/**
 * 
 * Class used by all noise functions to allow them to be called polymorphically.
 * 
 * @author F4113nb34st
 *
 */
public interface NoiseFunction
{
	/**
	 * Should fill the given array with the noise of this type.
	 * @param array The array to fill.
	 * @param seed The seed to seed the noise with.
	 * @param octave The octave of the noise.
	 */
	public void fillArray(NoiseArray array, long seed, int octave);
}
