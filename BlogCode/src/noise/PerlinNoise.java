package noise;

import util.FastMath;
import util.Interpolation;

/**
 * 
 * Class that generates 2D Perlin pseudo-random noise.
 * 
 * @author F4113nb34st
 *
 */
public final class PerlinNoise extends PeriodicNoise
{
	/**
	 * The NoiseGenerator instance for this PerlinNoise.
	 */
	private NoiseGenerator gen = new NoiseGenerator();
	
	/**
	 * Creates a new PerlinNoise with the given seed and periods.
	 * @param s The seed.
	 * @param px The x period.
	 * @param py The y period.
	 */
	public PerlinNoise(long s, int px, int py)
	{
		super(s, px, py);
	}
	
	@Override
	public void fillArray(NoiseArray noise)
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
	private double[][][] getGradientMap(int width, int height, long seed)
	{
		//create the map
		double[][][] gradients = new double[width][height][2];
		//for all values
		for(int i = 0; i < gradients.length; i++)
		{
			for(int j = 0; j < gradients[0].length; j++)
			{
				//get random angle for this point
				double angle = gen.noise_gen(seed, i, j) * Math.PI * 2;
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
	private double dotProduct(double[] gradient, double x, double y)
	{
		return (gradient[0] * x) + (gradient[1] * y);
	}
	
	/**
	 * Fade function. It is important not to use Math.pow, because it is not optimized for int powers.
	 * This version is ~100x faster than its Math.pow equivalent.
	 * @param value The value to fade.
	 * @return The faded value.
	 */
	private double fade(double value)
	{
		//return 6x^5 - 15x^4 + 10x^3;
		double val3 = value * value * value;
		double val4 = val3 * value;
		double val5 = val4 * value;
		return (6 * val5) - (15 * val4) + (10 * val3);
	}
}
