package noise;

import util.FastMath;
import util.Interpolation;
import util.concurrent.ArrayTask;
import util.concurrent.ThreadPool;

/**
 * 
 * Class that generates 2D Perlin pseudo-random noise.
 * 
 * @author F4113nb34st
 *
 */
public final class LookupPerlinNoise extends PeriodicNoise implements MultiThreadedNoise
{
	private static final double[][] gradients = new double[16][2];
	private static final int gradMask = gradients.length - 1;
	static
	{
		double theta = 0;
		double inc = Math.PI * 2 / gradients.length;
		for(int index = 0; index < gradients.length; index++, theta += inc)
		{
			gradients[index][0] = FastMath.cos(theta);
			gradients[index][1] = FastMath.sin(theta);
		}
	}
	/**
	 * The NoiseGenerator instance for this PerlinNoise.
	 */
	private NoiseGenerator gen = new NoiseGenerator();
	
	/**
	 * Creates a new PerlinNoise
	 */
	public LookupPerlinNoise()
	{
		this(0, 1, 1);
	}
	
	/**
	 * Creates a new PerlinNoise with the given seed and periods.
	 * @param s The seed.
	 * @param px The x period.
	 * @param py The y period.
	 */
	public LookupPerlinNoise(long s, int px, int py)
	{
		super(s, px, py);
	}
	
	@Override
	public PeriodicNoise copy()
	{
		return new LookupPerlinNoise(seed, periodX, periodY);
	}
	
	@Override
	public void fillArray(NoiseArray noise)
	{
		int[] permutation = new int[64];
		int permMask = permutation.length - 1;
		for(int i = 0; i < permutation.length; i++)
		{
			permutation[i] = i;
		}
		for(int i = 0; i < permutation.length; i++)
		{
			int index = (int)(gen.noise_gen(seed, i) * permutation.length);
			int value = permutation[i];
			
			permutation[i] = permutation[index];
			permutation[index] = value;
		}
		
		//for all columns
		for(int x = 0; x < noise.getWidth(); x++)
		{
			//find botX
			int botX = x / periodX;
			//find topX
			int topX = botX + 1;
			//find x fraction portion
			double fracX = (x % (double)periodX) / periodX;
			
			//for all rows
			for(int y = 0; y < noise.getHeight(); y++)
			{
				//find botY
				int botY = y / periodY;
				//find topY
				int topY = botY + 1;
				//find y fraction portion
				double fracY = (y % (double)periodY) / periodY;
				
				//find values for x's and y's
				double valBXBY = dotProduct(gradients[permutation[(botX + permutation[botY & permMask]) & permMask] & gradMask], fracX, fracY);
				double valTXBY = dotProduct(gradients[permutation[(topX + permutation[botY & permMask]) & permMask] & gradMask], fracX - 1D, fracY);
				double valBXTY = dotProduct(gradients[permutation[(botX + permutation[topY & permMask]) & permMask] & gradMask], fracX, fracY - 1D);
				double valTXTY = dotProduct(gradients[permutation[(topX + permutation[topY & permMask]) & permMask] & gradMask], fracX - 1D, fracY - 1D);
				
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

	@Override
	public void fillMultiThreaded(NoiseArray noise, ThreadPool pool)
	{
		int[] permutation = new int[64];
		for(int i = 0; i < permutation.length; i++)
		{
			permutation[i] = i;
		}
		for(int i = 0; i < permutation.length; i++)
		{
			int index = (int)(gen.noise_gen(seed, i) * permutation.length);
			int value = permutation[i];
			
			permutation[i] = permutation[index];
			permutation[index] = value;
		}
		
		pool.addGlobalTask(new ColumnTask(noise, permutation, 0, noise.getWidth() - 1));
		pool.startAndWait();
	}
	
	/**
	 * Task that calculates perlin noise columns.
	 */
	private class ColumnTask extends ArrayTask
	{
		private final NoiseArray noise;
		private final int[] permutation;
		private final int permMask;
		
		private ColumnTask(NoiseArray array, int[] perm, int min, int max)
		{
			super(min, max);
			noise = array;
			permutation = perm;
			permMask = permutation.length - 1;
		}

		@Override
		public void run(int x)
		{
			//find botX
			int botX = x / periodX;
			//find topX
			int topX = botX + 1;
			//find x fraction portion
			double fracX = (x % (double)periodX) / periodX;
			
			//for all rows
			for(int y = 0; y < noise.getHeight(); y++)
			{
				//find botY
				int botY = y / periodY;
				//find topY
				int topY = botY + 1;
				//find y fraction portion
				double fracY = (y % (double)periodY) / periodY;
				
				//find values for x's and y's
				double valBXBY = dotProduct(gradients[permutation[(botX + permutation[botY & permMask]) & permMask] & gradMask], fracX, fracY);
				double valTXBY = dotProduct(gradients[permutation[(topX + permutation[botY & permMask]) & permMask] & gradMask], fracX - 1D, fracY);
				double valBXTY = dotProduct(gradients[permutation[(botX + permutation[topY & permMask]) & permMask] & gradMask], fracX, fracY - 1D);
				double valTXTY = dotProduct(gradients[permutation[(topX + permutation[topY & permMask]) & permMask] & gradMask], fracX - 1D, fracY - 1D);
				
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
}
