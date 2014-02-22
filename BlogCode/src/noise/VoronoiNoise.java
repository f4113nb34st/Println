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
	public static final void fill_voronoi_noise_array(NoiseArray noise, long seed, int periodX, int periodY, DistanceFunction disFunc, CombineFunction comFunc)
	{
		int dotsX = (int)Math.ceil(noise.getWidth() / (double)periodX);
		int dotsY = (int)Math.ceil(noise.getHeight() / (double)periodY);
		
		double[][][] dots = new double[dotsX][dotsY][2];
		for(int i = 0; i < dots.length; i++)
		{
			for(int j = 0; j < dots[0].length; j++)
			{
				dots[i][j][0] = BasicNoise.noise_gen(i, j, 0, seed);//set x
				dots[i][j][1] = BasicNoise.noise_gen(i, j, 1, seed);//set y
			}
		}
		
		for(int x = 0; x < noise.getWidth(); x++)
		{
			int cellX = x / periodX;
			double fracX = (x % periodX) / (double)periodX;
			
			for(int y = 0; y < noise.getHeight(); y++)
			{
				int cellY = y / periodY;
				double fracY = (y % periodY) / (double)periodY;
				
				double[] minDis = new double[comFunc.getNumDistances()];//the dis to closest point
				for(int i = 0; i < minDis.length; i++)
				{
					minDis[i] = Double.POSITIVE_INFINITY;
				}
				
				for(int i = -1; i <= 1; i++)//check cell and neighbors
				{
					for(int j = -1; j <= 1; j++)//check cell and neighbors
					{
						double[] dot = dots[Util.wrap(cellX + i, 0, dots.length - 1)][Util.wrap(cellY + j, 0, dots[0].length - 1)];
						double disTo = disFunc.distanceFunc(fracX, fracY, dot[0] + i, dot[1] + j);
						insert(minDis, disTo);
					}
				}
				
				double value = comFunc.combineFunc(minDis);
				noise.set(x + noise.minX, y + noise.minY, value);
			}
		}
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
