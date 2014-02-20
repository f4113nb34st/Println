package noise;

import java.util.Random;

public class FractalNoise
{
	private static final Random rand = new Random();
	
	/**
	 * Generates a fractal noise array.
	 * @param width The width of the array.
	 * @param height The height of the array.
	 * @param seed The seed to seed the baseNoise array.
	 * @param func The noise function to use.
	 * @param fineOctave The minimum octave to use.
	 * @param broadOctave The maximum octave to use.
	 * @param persistence The persistence of the octaves.
	 * @return The resulting noise array.
	 */
	public static final NoiseArray fractal_noise_array(int width, int height, long seed, NoiseFunction func, int fineOctave, int broadOctave, double persistence)
	{
		NoiseArray noise = new NoiseArray(width, height);
		fill_fractal_noise_array(noise, seed, func, fineOctave, broadOctave, persistence);
		return noise;
	}
	
	/**
	 * Fills the given array with fractal noise.
	 * @param noise The noise array to fill.
	 * @param seed The seed to seed the octave arrays.
	 * @param func The noise function to use.
	 * @param fineOctave The minimum octave to use.
	 * @param broadOctave The maximum octave to use.
	 * @param persistence The persistence of the octaves.
	 */
	public static final void fill_fractal_noise_array(NoiseArray noise, long seed, NoiseFunction func, int fineOctave, int broadOctave, double persistence)
	{
		rand.setSeed(seed);
		NoiseArray[] octaves = new NoiseArray[broadOctave + 1 - fineOctave];
		
		for(int octave = broadOctave; octave >= fineOctave; octave--)
		{
			octaves[octave - fineOctave] = new NoiseArray(noise.getWidth(), noise.getHeight());
			func.fillArray(octaves[octave - fineOctave], rand.nextLong(), octave);
		}
		
		for(int x = noise.minX; x <= noise.maxX; x++)
		{
			for(int y = noise.minY; y <= noise.maxY; y++)
			{
				double value = 0;
				double currentAmp = 1;
				double totalAmp = 0;
				for(int octave = broadOctave; octave >= fineOctave; octave--)
				{
					value += octaves[octave - fineOctave].get(x, y) * currentAmp;
					totalAmp += currentAmp;
					currentAmp *= persistence;
				}
				value /= totalAmp;
				
				noise.set(x, y, value);
			}
		}
	}
}
