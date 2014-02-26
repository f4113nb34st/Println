package noise;

import java.util.Random;

/**
 * 
 * Stores methods for generating Fractal noise.
 * 
 * @author F4113nb34st
 *
 */
public class FractalNoise
{
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
		//create new array
		NoiseArray noise = new NoiseArray(width, height);
		//fill it
		fill_fractal_noise_array(noise, seed, func, fineOctave, broadOctave, persistence);
		//return it
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
		//create our personal random
		Random rand = new Random(seed);
		//create array of octaves
		NoiseArray[] octaves = new NoiseArray[broadOctave + 1 - fineOctave];
		
		//starting at top octave and going down
		for(int octave = broadOctave; octave >= fineOctave; octave--)
		{
			//create new array for octave
			octaves[octave - fineOctave] = new NoiseArray(noise.getWidth(), noise.getHeight());
			//fill from noise function with a random seed
			func.fillArray(octaves[octave - fineOctave], rand.nextLong(), octave);
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
}
