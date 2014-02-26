package noise;

import util.Util;
import noise.voronoi.CombineFunction;
import noise.voronoi.DistanceFunction;

/**
 * 
 * Stores static methods for filling NoiseArrays with simple (point srcs only) Voronoi noise.
 * 
 * @author F4113nb34st
 *
 */
public class VoronoiNoise implements NoiseFunction
{
	/**
	 * Generates a simple Voronoi noise array.
	 * @param width The width of the array.
	 * @param height The height of the array.
	 * @param seed The seed to seed the src points with.
	 * @param periodX The x period for the src points.
	 * @param periodY The y period for the src points.
	 * @param disFunc The distance function to use.
	 * @param comFunc The combine function to use.
	 * @return The resulting noise array.
	 */
	public static final NoiseArray voronoi_noise_array(int width, int height, long seed, int periodX, int periodY, DistanceFunction disFunc, CombineFunction comFunc)
	{
		//make new array
		NoiseArray noise = new NoiseArray(width, height);
		//fill it
		fill_voronoi_noise_array(noise, seed, periodX, periodY, disFunc, comFunc);
		//return it
		return noise;
	}
	
	/**
	 * Fills the given NoiseArray with simple Voronoi noise.
	 * @param noise The array to fill.
	 * @param seed The seed to seed the src points with.
	 * @param periodX The x period for the src points.
	 * @param periodY The y period for the src points.
	 * @param disFunc The distance function to use.
	 * @param comFunc The combine function to use.
	 */
	public static final void fill_voronoi_noise_array(NoiseArray noise, long seed, int periodX, int periodY, DistanceFunction disFunc, CombineFunction comFunc)
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
				dots[i][j][0] = BasicNoise.noise_gen(i, j, 0, seed);//set x
				dots[i][j][1] = BasicNoise.noise_gen(i, j, 1, seed);//set y
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
    private static void insert(double[] array, double value)
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

	/**
	 * Returns an interp noise function with the given interpolation function.
	 * @param inter The interpolation function to use.
	 * @return The noise function.
	 */
	public static final NoiseFunction getAsFunction(DistanceFunction disFunc, CombineFunction comFunc)
	{
		return new VoronoiNoise(disFunc, comFunc);
	}
	
	private DistanceFunction disFunc = DistanceFunction.Euclid;
	private CombineFunction comFunc = CombineFunction.F1;
	
	private VoronoiNoise(DistanceFunction disF, CombineFunction comF)
	{
		disFunc = disF;
		comFunc = comF;
	}

	@Override
	public void fillArray(NoiseArray array, long seed, int octave)
	{
		int period = 1 << octave;//2 ^ octave
		fill_voronoi_noise_array(array, seed, period, period, disFunc, comFunc);
	}
}
