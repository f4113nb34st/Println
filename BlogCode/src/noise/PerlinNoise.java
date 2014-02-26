package noise;

import util.FastMath;
import util.Interpolation;

/**
 * 
 * Stores methods for generating 2D Perlin pseudo-random noise.
 * 
 * @author F4113nb34st
 *
 */
public class PerlinNoise implements NoiseFunction
{
	/**
	 * Generates a Perlin noise array.
	 * @param width The width of the array.
	 * @param height The height of the array.
	 * @param seed The seed to seed the baseNoise array.
	 * @param periodX The x period of the smoothing.
	 * @param periodY The y period of the smoothing.
	 * @return The resulting noise array.
	 */
	public static final NoiseArray perlin_noise_array(int width, int height, long seed, int periodX, int periodY)
	{
		//create new array
		NoiseArray noise = new NoiseArray(width, height);
		//fill it
		fill_perlin_noise_array(noise, seed, periodX, periodY);
		//return it
		return noise;
	}
	
	/**
	 * Fills the given array with Perlin noise.
	 * @param noise The noise array to fill.
	 * @param seed The seed to seed the baseNoise array should it need to be generated.
	 * @param periodX The x period of the smoothing.
	 * @param periodY The y period of the smoothing.
	 */
	public static final void fill_perlin_noise_array(NoiseArray noise, long seed, int periodX, int periodY)
	{
		//calculate the base width and height (no need to calculate more values than this in base array)
		int baseW = (int)Math.ceil(noise.getWidth() / (double)periodX);
		int baseH = (int)Math.ceil(noise.getHeight() / (double)periodY);
		
		//create our gradient map
		double[][][] gradients = getGradientMap(baseW, baseH, seed);
		
		//for all columns
		for(int x = 0; x < noise.getWidth(); x++)
		{
			//find botX
			int botX = x / periodX;
			//find topX
			int topX = (botX + 1) % gradients.length;
			//find x fraction portion
			double fracX = (x % (double)periodX) / periodX;
			
			//for all rows
			for(int y = 0; y < noise.getHeight(); y++)
			{
				//find botY
				int botY = y / periodY;
				//find topY
				int topY = (botY + 1) % gradients[0].length;
				//find y fraction portion
				double fracY = (y % (double)periodY) / periodY;
				
				//find values for x's and y's
				double valBXBY = dotProduct(gradients[botX][botY], fracX, fracY);
				double valTXBY = dotProduct(gradients[topX][botY], fracX - 1D, fracY);
				double valBXTY = dotProduct(gradients[botX][topY], fracX, fracY - 1D);
				double valTXTY = dotProduct(gradients[topX][topY], fracX - 1D, fracY - 1D);
				
				//fade fracs
				double newFracX = fade(fracX);
				double newFracY = fade(fracY);
				
				//perform y interps
				double yBotInterp = Interpolation.LINEAR.interpolate(valBXBY, valTXBY, newFracX);
				double yTopInterp = Interpolation.LINEAR.interpolate(valBXTY, valTXTY, newFracX);
				
				//set value
				noise.setRelative(x, y, Interpolation.LINEAR.interpolate(yBotInterp, yTopInterp, newFracY));
			}
		}
	}
	
	/**
	 * Generates a gradient map from the given width, height and seed.
	 * @param width The width of the gradient map.
	 * @param height The height of the gradient map.
	 * @param seed The seed to seed map with.
	 * @return The resulting gradient map.
	 */
	private static final double[][][] getGradientMap(int width, int height, long seed)
	{
		//create the map
		double[][][] gradients = new double[width][height][2];
		//for all values
		for(int i = 0; i < gradients.length; i++)
		{
			for(int j = 0; j < gradients[0].length; j++)
			{
				//get random angle for this point
				double angle = BasicNoise.noise_gen(i, j, seed) * Math.PI * 2;
				//set x and y
				gradients[i][j][0] = FastMath.cos(angle);
				gradients[i][j][1] = FastMath.sin(angle);
			}
		}
		//return map
		return gradients;
	}
	
	/**
	 * Performs a dotProduct on the given gradient and point.
	 * @param gradient The gradient.
	 * @param x The x value of the point.
	 * @param y The y value of the point.
	 * @return The dot product.
	 */
	private static final double dotProduct(double[] gradient, double x, double y)
	{
		return (gradient[0] * x) + (gradient[1] * y);
	}
	
	/**
	 * Fade function. It is important not to use Math.pow, because it is not optimized for int powers.
	 * This version is ~100x faster than its Math.pow equivalent.
	 * @param value The value to fade.
	 * @return The faded value.
	 */
	private static final double fade(double value)
	{
		//return 6x^5 - 15x^4 + 10x^3;
		double val3 = value * value * value;
		double val4 = val3 * value;
		double val5 = val4 * value;
		return (6 * val5) - (15 * val4) + (10 * val3);
	}
	
	/**
	 * Returns an interp noise function with the given interpolation function.
	 * @param inter The interpolation function to use.
	 * @return The noise function.
	 */
	public static final NoiseFunction getAsFunction()
	{
		return new PerlinNoise();
	}

	@Override
	public void fillArray(NoiseArray array, long seed, int octave)
	{
		int period = 1 << octave;//2 ^ octave
		fill_perlin_noise_array(array, seed, period, period);
	}
}
