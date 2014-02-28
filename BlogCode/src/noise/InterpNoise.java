package noise;

import util.Interpolation;
import util.concurrent.ArrayTask;
import util.concurrent.ThreadPool;

/**
 * 
 * Class that generates interpolated pseudo-random noise.
 * 
 * @author F4113nb34st
 *
 */
public final class InterpNoise extends PeriodicNoise implements MultiThreadedNoise
{
	/**
	 * The interpolation function to use.
	 */
	public Interpolation interp;
	
	/**
	 * The BasicNoise instance for this InterpNoise.
	 */
	private BasicNoise basic;
	
	/**
	 * Creates a new InterpNoise with the given interpolation function.
	 * @param inter The interpolation function.
	 */
	public InterpNoise(Interpolation inter)
	{
		this(0, 1, 1, inter);
	}
	
	/**
	 * Creates a new InterpNoise with the given seed, periods, and interpolation function.
	 * @param s The seed.
	 * @param px The x period.
	 * @param py The y period.
	 * @param inter The interpolation function.
	 */
	public InterpNoise(long s, int px, int py, Interpolation inter)
	{
		super(s, px, py);
		interp = inter;
		basic = new BasicNoise(0);
	}
	
	@Override
	public PeriodicNoise copy()
	{
		return new InterpNoise(seed, periodX, periodY, interp);
	}

	@Override
	public void fillArray(NoiseArray noise)
	{
		//calculate the base width and height (no need to calculate more values than this in base array)
		int baseW = (int)Math.ceil(noise.getWidth() / (double)periodX);
		int baseH = (int)Math.ceil(noise.getHeight() / (double)periodY);
		//create our base noise array
		NoiseArray baseNoise = new NoiseArray(baseW, baseH);
		//update basic's seed
		basic.seed = seed;
		//fill with basic noise
		basic.fillArray(baseNoise);
		
		//if only needs top and bottom values
		if(!interp.extended())
		{
			//for all columns
			for(int x = 0; x < noise.getWidth(); x++)
			{
				//get botX
				int bottomX = (int)(x / periodX);
				//get topX
				int topX = bottomX + 1;
				//get blend part for x
				double blendX = (x % periodX) / (double)periodX;
				
				//for all rows
				for(int y = 0; y < noise.getHeight(); y++)
				{
					//get botY
					int bottomY = (int)(y / periodY);
					//get topY
					int topY = bottomY + 1;
					//get blend part for y
					double blendY = (y % periodY) / (double)periodY;
					
					//interp between xbots and xtops
					double xBotInterp = interp.interpolate(baseNoise.get(bottomX, bottomY), baseNoise.get(bottomX, topY), blendY);
					double xTopInterp = interp.interpolate(baseNoise.get(topX, bottomY), baseNoise.get(topX, topY), blendY);
					
					//interp interps
					noise.setRelative(x, y, interp.interpolate(xBotInterp, xTopInterp, blendX));
				}
			}
		}else//we need past and future values too
		{
			//for all columns
			for(int x = 0; x < noise.getWidth(); x++)
			{
				//get botX
				int bottomX = (int)(x / periodX);
				//get topX
				int topX = bottomX + 1;
				//get pastX
				int pastX = bottomX - 1;
				//get futureX
				int futureX = topX + 1;
				//get blend part for x
				double blendX = (x % periodX) / (double)periodX;
				
				for(int y = 0; y < noise.getHeight(); y++)
				{
					//get botY
					int bottomY = (int)(y / periodY);
					//get topY
					int topY = bottomY + 1;
					//get pastY
					int pastY = bottomY - 1;
					//get futureY
					int futureY = topY + 1;
					//get blend part for y
					double blendY = (y % periodY) / (double)periodY;
					
					//interp between xbots, xtops, xpasts, and xfutures
					double xPastInterp = interp.interpolate(baseNoise.get(pastX, pastY), baseNoise.get(pastX, bottomY), baseNoise.get(pastX, topY), baseNoise.get(pastX, futureY), blendY);
					double xBotInterp = interp.interpolate(baseNoise.get(bottomX, pastY), baseNoise.get(bottomX, bottomY), baseNoise.get(bottomX, topY), baseNoise.get(bottomX, futureY), blendY);
					double xTopInterp = interp.interpolate(baseNoise.get(topX, pastY), baseNoise.get(topX, bottomY), baseNoise.get(topX, topY), baseNoise.get(topX, futureY), blendY);
					double xFutureInterp = interp.interpolate(baseNoise.get(futureX, pastY), baseNoise.get(futureX, bottomY), baseNoise.get(futureX, topY), baseNoise.get(futureX, futureY), blendY);
					
					//interp interps
					noise.setRelative(x, y, interp.interpolate(xPastInterp, xBotInterp, xTopInterp, xFutureInterp, blendX));
				}
			}
		}
	}
	
	public void fillMultiThreaded(NoiseArray noise, ThreadPool pool)
	{
		//calculate the base width and height (no need to calculate more values than this in base array)
		int baseW = (int)Math.ceil(noise.getWidth() / (double)periodX);
		int baseH = (int)Math.ceil(noise.getHeight() / (double)periodY);
		//create our base noise array
		NoiseArray baseNoise = new NoiseArray(baseW, baseH);
		//update basic's seed
		basic.seed = seed;
		//fill with basic noise
		basic.fillMultiThreaded(baseNoise, pool);
		
		//if only needs top and bottom values
		if(!interp.extended())
		{
			pool.addGlobalTask(new ColumnTask(noise, baseNoise, 0, noise.getWidth() - 1));
		}else//we need past and future values too
		{
			pool.addGlobalTask(new ExtendedColumnTask(noise, baseNoise, 0, noise.getWidth() - 1));
		}
		pool.startAndWait();
	}
	
	/**
	 * Task that calculates interpolated noise columns.
	 */
	private class ColumnTask extends ArrayTask
	{
		private final NoiseArray noise;
		private final NoiseArray baseNoise;
		
		private ColumnTask(NoiseArray array, NoiseArray base, int min, int max)
		{
			super(min, max);
			noise = array;
			baseNoise = base;
		}

		@Override
		public void run(int x)
		{
			//get botX
			int bottomX = (int)(x / periodX);
			//get topX
			int topX = bottomX + 1;
			//get blend part for x
			double blendX = (x % periodX) / (double)periodX;
			
			//for all rows
			for(int y = 0; y < noise.getHeight(); y++)
			{
				//get botY
				int bottomY = (int)(y / periodY);
				//get topY
				int topY = bottomY + 1;
				//get blend part for y
				double blendY = (y % periodY) / (double)periodY;
				
				//interp between xbots and xtops
				double xBotInterp = interp.interpolate(baseNoise.get(bottomX, bottomY), baseNoise.get(bottomX, topY), blendY);
				double xTopInterp = interp.interpolate(baseNoise.get(topX, bottomY), baseNoise.get(topX, topY), blendY);
				
				//interp interps
				noise.setRelative(x, y, interp.interpolate(xBotInterp, xTopInterp, blendX));
			}
		}
	}
	
	/**
	 * Task that calculates extended interpolated noise columns.
	 */
	private class ExtendedColumnTask extends ArrayTask
	{
		private final NoiseArray noise;
		private final NoiseArray baseNoise;
		
		private ExtendedColumnTask(NoiseArray array, NoiseArray base, int min, int max)
		{
			super(min, max);
			noise = array;
			baseNoise = base;
		}

		@Override
		public void run(int x)
		{
			//get botX
			int bottomX = (int)(x / periodX);
			//get topX
			int topX = bottomX + 1;
			//get pastX
			int pastX = bottomX - 1;
			//get futureX
			int futureX = topX + 1;
			//get blend part for x
			double blendX = (x % periodX) / (double)periodX;
			
			for(int y = 0; y < noise.getHeight(); y++)
			{
				//get botY
				int bottomY = (int)(y / periodY);
				//get topY
				int topY = bottomY + 1;
				//get pastY
				int pastY = bottomY - 1;
				//get futureY
				int futureY = topY + 1;
				//get blend part for y
				double blendY = (y % periodY) / (double)periodY;
				
				//interp between xbots, xtops, xpasts, and xfutures
				double xPastInterp = interp.interpolate(baseNoise.get(pastX, pastY), baseNoise.get(pastX, bottomY), baseNoise.get(pastX, topY), baseNoise.get(pastX, futureY), blendY);
				double xBotInterp = interp.interpolate(baseNoise.get(bottomX, pastY), baseNoise.get(bottomX, bottomY), baseNoise.get(bottomX, topY), baseNoise.get(bottomX, futureY), blendY);
				double xTopInterp = interp.interpolate(baseNoise.get(topX, pastY), baseNoise.get(topX, bottomY), baseNoise.get(topX, topY), baseNoise.get(topX, futureY), blendY);
				double xFutureInterp = interp.interpolate(baseNoise.get(futureX, pastY), baseNoise.get(futureX, bottomY), baseNoise.get(futureX, topY), baseNoise.get(futureX, futureY), blendY);
				
				//interp interps
				noise.setRelative(x, y, interp.interpolate(xPastInterp, xBotInterp, xTopInterp, xFutureInterp, blendX));
			}
		}
	}
}
