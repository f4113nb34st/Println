package noise;

import util.Interpolation;

/**
 * 
 * Stores methods for generating interpolated pseudo-random noise.
 * 
 * @author F4113nb34st
 *
 */
public class InterpNoise implements NoiseFunction
{
	/**
	 * Generates a interpolated noise array.
	 * @param width The width of the array.
	 * @param height The height of the array.
	 * @param seed The seed to seed the baseNoise array.
	 * @param interp The interpolation function to use.
	 * @param periodX The x period of the smoothing.
	 * @param periodY The y period of the smoothing.
	 * @return The resulting noise array.
	 */
	public static final NoiseArray interp_noise_array(int width, int height, long seed, Interpolation interp, int periodX, int periodY)
	{
		NoiseArray noise = new NoiseArray(width, height);
		fill_interp_noise_array(noise, null, seed, interp, periodX, periodY);
		return noise;
	}
	
	/**
	 * Fills the given array with interpolated noise.
	 * @param noise The noise array to fill.
	 * @param baseNoise The base noise array (dimensions should be at least (noise.x / periodX, noise.y / periodY)).
	 * 			If null, will be generated with the seed given.
	 * @param seed The seed to seed the baseNoise array should it need to be generated.
	 * @param interp The interpolation function to use.
	 * @param periodX The x period of the smoothing.
	 * @param periodY The y period of the smoothing.
	 */
	public static final void fill_interp_noise_array(NoiseArray noise, NoiseArray baseNoise, long seed, Interpolation interp, int periodX, int periodY)
	{
		int baseW = (int)Math.ceil(noise.getWidth() / (double)periodX);
		int baseH = (int)Math.ceil(noise.getHeight() / (double)periodY);
		if(baseNoise == null)
		{
			baseNoise = new NoiseArray(baseW, baseH);
		}else
		{
			baseNoise.setBounds(baseW, baseH);
		}
		BasicNoise.fill_noise_array(baseNoise, seed);
		
		if(!interp.extended())
		{
			for(int x = noise.minX; x <= noise.maxX; x++)
			{
				int bottomX = (int)(x / periodX);
				int topX = bottomX + 1;
				double blendX = (x % periodX) / (double)periodX;
				
				for(int y = noise.minY; y <= noise.maxY; y++)
				{
					int bottomY = (int)(y / periodY);
					int topY = bottomY + 1;
					double blendY = (y % periodY) / (double)periodY;
					
					double xBotInterp = interp.interpolate(baseNoise.get(bottomX, bottomY), baseNoise.get(bottomX, topY), blendY);
					double xTopInterp = interp.interpolate(baseNoise.get(topX, bottomY), baseNoise.get(topX, topY), blendY);
					
					noise.set(x, y, interp.interpolate(xBotInterp, xTopInterp, blendX));
				}
			}
		}else
		{
			for(int x = noise.minX; x <= noise.maxX; x++)
			{
				int bottomX = (int)(x / periodX);
				int topX = bottomX + 1;
				int pastX = bottomX - 1;
				int futureX = topX + 1;
				double blendX = (x % periodX) / (double)periodX;
				
				for(int y = noise.minY; y <= noise.maxY; y++)
				{
					int bottomY = (int)(y / periodY);
					int topY = bottomY + 1;
					int pastY = bottomY - 1;
					int futureY = topY + 1;
					double blendY = (y % periodY) / (double)periodY;
					
					double xPastInterp = interp.interpolate(baseNoise.get(pastX, pastY), baseNoise.get(pastX, bottomY), baseNoise.get(pastX, topY), baseNoise.get(pastX, futureY), blendY);
					double xBotInterp = interp.interpolate(baseNoise.get(bottomX, pastY), baseNoise.get(bottomX, bottomY), baseNoise.get(bottomX, topY), baseNoise.get(bottomX, futureY), blendY);
					double xTopInterp = interp.interpolate(baseNoise.get(topX, pastY), baseNoise.get(topX, bottomY), baseNoise.get(topX, topY), baseNoise.get(topX, futureY), blendY);
					double xFutureInterp = interp.interpolate(baseNoise.get(futureX, pastY), baseNoise.get(futureX, bottomY), baseNoise.get(futureX, topY), baseNoise.get(futureX, futureY), blendY);
					
					noise.set(x, y, interp.interpolate(xPastInterp, xBotInterp, xTopInterp, xFutureInterp, blendX));
				}
			}
		}
	}
	
	/**
	 * Returns an interp noise function with the given interpolation function.
	 * @param inter The interpolation function to use.
	 * @return The noise function.
	 */
	public static final NoiseFunction getAsFunction(Interpolation inter)
	{
		return new InterpNoise(inter);
	}
	
	private Interpolation inter = Interpolation.LINEAR;
	
	private InterpNoise(Interpolation in)
	{
		inter = in;
	}

	@Override
	public void fillArray(NoiseArray array, long seed, int octave)
	{
		int period = 1 << octave;//2 ^ octave
		fill_interp_noise_array(array, null, seed, inter, period, period);
	}
}
