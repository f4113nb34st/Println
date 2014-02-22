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
		NoiseArray noise = new NoiseArray(width, height);
		fill_perlin_noise_array(noise, seed, periodX, periodY);
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
		double[][][] gradients = getGradientMap((int)Math.ceil(noise.getWidth() / (double)periodX), (int)Math.ceil(noise.getHeight() / (double)periodY), seed);
		
		for(int x = 0; x < noise.getWidth(); x++)
		{
			int i = x / periodX;
			double fracX = (x % (double)periodX) / periodX;
			int botX = i;
			int topX = (i + 1) % gradients.length;
			
			for(int y = 0; y < noise.getHeight(); y++)
			{
				int j = y / periodY;
				double fracY = (y % (double)periodY) / periodY;
				int botY = j;
				int topY = (j + 1) % gradients[0].length;
				
				double valBXBY = dotProduct(gradients[botX][botY], fracX, fracY);
				double valTXBY = dotProduct(gradients[topX][botY], fracX - 1D, fracY);
				double valBXTY = dotProduct(gradients[botX][topY], fracX, fracY - 1D);
				double valTXTY = dotProduct(gradients[topX][topY], fracX - 1D, fracY - 1D);
				
				double newFracX = fade(fracX);
				double newFracY = fade(fracY);
				
				double yBotInterp = Interpolation.LINEAR.interpolate(valBXBY, valTXBY, newFracX);
				double yTopInterp = Interpolation.LINEAR.interpolate(valBXTY, valTXTY, newFracX);
				
				noise.setRelative(x, y, Interpolation.LINEAR.interpolate(yBotInterp, yTopInterp, newFracY));
			}
		}
	}
	
	private static final double[][][] getGradientMap(int width, int height, long seed)
	{
		double[][][] gradients = new double[width][height][2];
		for(int i = 0; i < gradients.length; i++)
		{
			for(int j = 0; j < gradients[0].length; j++)
			{
				double angle = BasicNoise.noise_gen(i, j, seed) * Math.PI * 2;
				gradients[i][j][0] = FastMath.cos(angle);
				gradients[i][j][1] = FastMath.sin(angle);
			}
		}
		return gradients;
	}
	
	private static final double dotProduct(double[] gradient, double x, double y)
	{
		return (gradient[0] * x) + (gradient[1] * y);
	}
	
	private static final double fade(double value)
	{
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
