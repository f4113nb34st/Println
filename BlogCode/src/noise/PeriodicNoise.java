package noise;

/**
 * 
 * Superclass of all noise that uses periods for generation.
 * 
 * @author F4113nb34st
 *
 */
public abstract class PeriodicNoise extends SeededNoise
{
	/**
	 * The x period of this noise.
	 */
	public int periodX;
	/**
	 * The y period of this noise.
	 */
	public int periodY;
	
	/**
	 * Creates a new PeriodicNoise with the given seed and periods.
	 * @param s The seed.
	 * @param px The x period.
	 * @param py The y period.
	 */
	public PeriodicNoise(long s, int px, int py)
	{
		super(s);
		periodX = px;
		periodY = py;
	}
	
	/**
	 * Fills the given array with noise of the given seed and octave.
	 * Used by FractalNoise.
	 * @param array The array to fill.
	 * @param s The seed to use.
	 * @param octave The octave of the noise.
	 */
	protected void fillArray(NoiseArray array, long s, int octave)
	{
		seed = s;
		periodX = periodY = 1 << octave;
		fillArray(array);
	}
}
