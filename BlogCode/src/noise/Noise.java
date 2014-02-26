package noise;

/**
 * 
 * Superclass of all noise generators.
 * 
 * @author F4113nb34st
 *
 */
public abstract class Noise
{
	/**
	 * Fills the given NoiseArray with this noise.
	 * @param array The array to fill.
	 */
	public abstract void fillArray(NoiseArray noise);
}
