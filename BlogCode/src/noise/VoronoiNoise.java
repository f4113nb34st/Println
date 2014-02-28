package noise;

import util.Util;
import util.concurrent.ArrayTask;
import util.concurrent.ThreadPool;
import noise.voronoi.CombineFunction;
import noise.voronoi.DistanceFunction;

/**
 * 
 * Stores static methods for filling NoiseArrays with simple (point srcs only) Voronoi noise.
 * 
 * @author F4113nb34st
 *
 */
public final class VoronoiNoise extends PeriodicNoise implements MultiThreadedNoise
{
	/**
	 * The NoiseGenerator instance for this VoronoiNoise.
	 */
	private NoiseGenerator gen = new NoiseGenerator();
	/**
	 * The distance function to use for the Voronoi generation.
	 */
	public DistanceFunction disFunc;
	/**
	 * The combine function to use for the Voronoi generation.
	 */
	public CombineFunction comFunc;
	
	/**
	 * Creates a new VoronoiNoise with the given distance function and combine function.
	 * @param disF The distance function.
	 * @param comF The combine function.
	 */
	public VoronoiNoise(DistanceFunction disF, CombineFunction comF)
	{
		this(0, 1, 1, disF, comF);
	}
	
	/**
	 * Creates a new VoronoiNoise with the given seed, periods, distance function and combine function.
	 * @param s The seed.
	 * @param px The x period.
	 * @param py The y period.
	 * @param disF The distance function.
	 * @param comF The combine function.
	 */
	public VoronoiNoise(long s, int px, int py, DistanceFunction disF, CombineFunction comF)
	{
		super(s, px, py);
		disFunc = disF;
		comFunc = comF;
	}
	
	@Override
	public PeriodicNoise copy()
	{
		return new VoronoiNoise(seed, periodX, periodY, disFunc, comFunc);
	}

	@Override
	public void fillArray(NoiseArray noise)
	{
		//get number of dots in x dimention
		int dotsX = (int)Math.ceil(noise.getWidth() / (double)periodX);
		//get number of dots in y dimention
		int dotsY = (int)Math.ceil(noise.getHeight() / (double)periodY);
		
		//create dot array
		double[][][] dots = new double[dotsX][dotsY][2];
		for(int i = 0; i < dots.length; i++)
		{
			for(int j = 0; j < dots[0].length; j++)
			{
				dots[i][j][0] = gen.noise_gen(seed, i, j, 0);//set x
				dots[i][j][1] = gen.noise_gen(seed, i, j, 1);//set y
			}
		}
		
		//for all columns.
		for(int x = 0; x < noise.getWidth(); x++)
		{
			//get x cell
			int cellX = x / periodX;
			//get fractional x part
			double fracX = (x % periodX) / (double)periodX;
			
			//for all rows
			for(int y = 0; y < noise.getHeight(); y++)
			{
				//get y cell
				int cellY = y / periodY;
				//get fractional y part
				double fracY = (y % periodY) / (double)periodY;
				
				//create minDis array
				double[] minDis = new double[comFunc.getNumDistances()];
				//init values to infinity
				for(int i = 0; i < minDis.length; i++)
				{
					minDis[i] = Double.POSITIVE_INFINITY;
				}
				
				//the distance we will check for points
				int checkDis = 1;
				//Mink. and anything with F3 needs more range
				if(disFunc == DistanceFunction.Minkowski0_5 || minDis.length > 2)
				{
					checkDis = 2;
				}
				
				//check cell and neighbors
				for(int i = -checkDis; i <= checkDis; i++)
				{
					for(int j = -checkDis; j <= checkDis; j++)
					{
						//get dot for current cell
						double[] dot = dots[Util.wrap(cellX + i, 0, dots.length - 1)][Util.wrap(cellY + j, 0, dots[0].length - 1)];
						//get distance to src point
						double disTo = disFunc.distanceFunc(fracX, fracY, dot[0] + i, dot[1] + j);
						//insert it into the sorted distance array
						insert(minDis, disTo);
					}
				}
				
				//perform the Euclid sqrts
				if(disFunc == DistanceFunction.Euclid)
				{
					for(int i = 0; i < minDis.length; i++)
					{
						minDis[i] = Math.sqrt(minDis[i]);
					}
				}
				//get value from distances
				double value = comFunc.combineFunc(minDis);
				//set value
				noise.setRelative(x, y, value);
			}
		}
		
		//always need to normalize Voronoi noise
		noise.normalize();
	}
	
	/**
     * Inserts a value into an array so that the array is sorted from least to greatest.
     * If the value is greater than the max value, it is not added.
     */
    private void insert(double[] array, double value)
    {
   	 	double temp;
        for(int i = array.length - 1; i >= 0; i--)
        {
            if(value > array[i]) 
            {
           	 	break;
            }
            temp = array[i];
            array[i] = value;
            if (i + 1 < array.length) 
            {
           	 	array[i + 1] = temp;
            }
        }
    }

	@Override
	public void fillMultiThreaded(NoiseArray noise, ThreadPool pool)
	{
		//get number of dots in x dimention
		int dotsX = (int)Math.ceil(noise.getWidth() / (double)periodX);
		//get number of dots in y dimention
		int dotsY = (int)Math.ceil(noise.getHeight() / (double)periodY);
		
		//create dot array
		double[][][] dots = new double[dotsX][dotsY][2];
		
		pool.addGlobalTask(new DotColumnTask(dots, 0, dots.length - 1));
		pool.startAndWait();
		
		pool.addGlobalTask(new ColumnTask(noise, dots, 0, noise.getWidth() - 1));
		pool.startAndWait();
		
		//always need to normalize Voronoi noise
		noise.normalize();
	}
	
	/**
	 * Task that calculates voronoi noise columns.
	 */
	private class ColumnTask extends ArrayTask
	{
		private final NoiseArray noise;
		private final double[][][] dots;
		
		private ColumnTask(NoiseArray array, double[][][] ds, int min, int max)
		{
			super(min, max);
			noise = array;
			dots = ds;
		}

		@Override
		public void run(int x)
		{
			//get x cell
			int cellX = x / periodX;
			//get fractional x part
			double fracX = (x % periodX) / (double)periodX;
			
			//for all rows
			for(int y = 0; y < noise.getHeight(); y++)
			{
				//get y cell
				int cellY = y / periodY;
				//get fractional y part
				double fracY = (y % periodY) / (double)periodY;
				
				//create minDis array
				double[] minDis = new double[comFunc.getNumDistances()];
				//init values to infinity
				for(int i = 0; i < minDis.length; i++)
				{
					minDis[i] = Double.POSITIVE_INFINITY;
				}
				
				//the distance we will check for points
				int checkDis = 1;
				//Mink. and anything with F3 needs more range
				if(disFunc == DistanceFunction.Minkowski0_5 || minDis.length > 2)
				{
					checkDis = 2;
				}
				
				//check cell and neighbors
				for(int i = -checkDis; i <= checkDis; i++)
				{
					for(int j = -checkDis; j <= checkDis; j++)
					{
						//get dot for current cell
						double[] dot = dots[Util.wrap(cellX + i, 0, dots.length - 1)][Util.wrap(cellY + j, 0, dots[0].length - 1)];
						//get distance to src point
						double disTo = disFunc.distanceFunc(fracX, fracY, dot[0] + i, dot[1] + j);
						//insert it into the sorted distance array
						insert(minDis, disTo);
					}
				}
				
				//perform the Euclid sqrts
				if(disFunc == DistanceFunction.Euclid)
				{
					for(int i = 0; i < minDis.length; i++)
					{
						minDis[i] = Math.sqrt(minDis[i]);
					}
				}
				//get value from distances
				double value = comFunc.combineFunc(minDis);
				//set value
				noise.setRelative(x, y, value);
			}
		}
	}
	
	private class DotColumnTask extends ArrayTask
	{
		private final double[][][] dots;
		
		private DotColumnTask(double[][][] ds, int min, int max)
		{
			super(min, max);
			dots = ds;
		}

		@Override
		public void run(int i)
		{
			for(int j = 0; j < dots[0].length; j++)
			{
				dots[i][j][0] = gen.noise_gen(seed, i, j, 0);//set x
				dots[i][j][1] = gen.noise_gen(seed, i, j, 1);//set y
			}
		}
	}
}
