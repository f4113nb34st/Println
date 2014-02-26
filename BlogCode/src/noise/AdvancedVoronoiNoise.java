package noise;

import java.util.ArrayList;
import java.util.HashSet;
import util.Util;
import math.Point2D;
import noise.voronoi.*;

/**
 * 
 * Stores static methods for filling NoiseArrays with advanced Voronoi noise.
 * 
 * @author F4113nb34st
 *
 */
public final class AdvancedVoronoiNoise
{
	
	/**
	 * Generates an advanced Voronoi noise array.
	 * @param width The width of the array.
	 * @param height The height of the array.
	 * @param objects The list of Voronoi objects to generate the array from.
	 * @param disFunc The distance function to use.
	 * @param comFunc The combine function to use.
	 * @return The resulting noise array.
	 */
	public static final NoiseArray adv_voronoi_noise_array(int width, int height, ArrayList<VoronoiObject> objects, DistanceFunction disFunc, CombineFunction comFunc)
	{
		//make new array
		NoiseArray noise = new NoiseArray(width, height);
		//fill it
		fill_adv_voronoi_noise_array(noise, objects, disFunc, comFunc);
		//return it
		return noise;
	}
	
	/**
	 * Fills a noise array with advanced Voronoi noise.
	 * @param width The width of the array.
	 * @param height The height of the array.
	 * @param objects The list of Voronoi objects to generate the array from.
	 * @param disFunc The distance function to use.
	 * @param comFunc The combine function to use.
	 */
	public static final void fill_adv_voronoi_noise_array(NoiseArray noise, ArrayList<VoronoiObject> objects, DistanceFunction disFunc, CombineFunction comFunc)
	{
		//we do all the calculations in a local instance instead of static variables to make things thread-safe
		new AdvancedVoronoiNoise(disFunc, comFunc).fill_adv_voronoi_noise_array(noise, objects);
	}
	
	/**
	 * Simple class that stores a point and a src object.
	 */
	public static class Location
	{
		private Point2D point;
		private VoronoiObject src;
		
		public Location(Point2D p, VoronoiObject s)
		{
			point = p;
			src = s;
		}
		
		/**
		 * Overridden equals to make it play nice with hashmaps.
		 */
		@Override
		public boolean equals(Object obj)
		{
			return obj.hashCode() == hashCode();
		}
		
		/**
		 * Overridden hashcode to make it play nice with hashmaps.
		 */
		@Override
		public int hashCode()
		{
			int multi = 451584;
			int value = 0;
			value = (value * multi) + point.hashCode();
			value = (value * multi) + src.hashCode();
		    return value;
		}
	}
	
	//our distance function
	public DistanceFunction disFunc;
	//our combine function
	public CombineFunction comFunc;
	//all past calculated values
	public HashSet<Location> past;
	//all values we are currently calculating
	public HashSet<Location> current;
	//all values to be calculated next generation
	public HashSet<Location> flagged;
	//the pixels and their closest values
	public double[][][] pixels;
	
	private AdvancedVoronoiNoise(DistanceFunction dis, CombineFunction com)
	{
		disFunc = dis;
		comFunc = com;
	}
	
	/**
	 * Fills the given noise array with Voronoi noise from the given objects using the local distance and combine functions.
	 * @param noise The noise array to fill.
	 * @param objects The objects to generate the noise from.
	 */
	public void fill_adv_voronoi_noise_array(NoiseArray noise, ArrayList<VoronoiObject> objects)
	{
		//create our pixel array.
		pixels = new double[noise.getWidth()][noise.getHeight()][comFunc.getNumDistances()];
		//init it to infinity at each distance
		for(int i = 0; i < pixels.length; i++)
		{
			for(int j = 0; j < pixels[0].length; j++)
			{
				for(int k = 0; k < pixels[0][0].length; k++)
				{
					pixels[i][j][k] = Double.POSITIVE_INFINITY;
				}
			}
		}
		
		//create past and flagged hashsets
		past = new HashSet<Location>();
		flagged = new HashSet<Location>();
		//list of all centers of the objects
		HashSet<Point2D> centers = new HashSet<Point2D>();
		for(VoronoiObject obj : objects)
		{
			//clear previous centers
			centers.clear();
			//add all from the current object
			obj.addCenters(centers);
			//for all centers 
			for(Point2D point : centers)
			{
				//if within bounds
				if(point.x >= 0 && point.y >= 0 && point.x < pixels.length && point.y < pixels[0].length)
				{
					//add the location
					flagged.add(new Location(point, obj));
				}
			}
		}
		//while more generations
		while(!flagged.isEmpty())
		{
			//set current generation to the flagged one
			current = flagged;
			//mark all current values as calculated
			past.addAll(current);
			//make new flagged generation
			flagged = new HashSet<Location>();
			//for all values in the current generation
			for(Location loc : current)
			{
				//set the value and flag neighbors
				setAndProp(loc);
			}
		}
		
		//for each pixe;s
		for(int x = 0; x < noise.getWidth(); x++)
		{
			for(int y = 0; y < noise.getHeight(); y++)
			{
				//get distances at current pixel
				double[] values = pixels[x][y];
				//perform euclid distance sqrts
				if(disFunc == DistanceFunction.Euclid)
				{
					for(int i = 0; i < values.length; i++)
					{
						values[i] = Math.sqrt(values[i]);
					}
				}
				//set to the combine function's return
				noise.setRelative(x, y, comFunc.combineFunc(values));
			}
		}
		
		//always need to normalize Voronoi noise
		noise.normalize();
	}
	
	/**
	 * Adds the distance to the location's src to the location's distances, and if it was one of the mins, flag neighbors.
	 * @param loc The location to process.
	 */
	private void setAndProp(Location loc)
	{
		//get value for the current point
		double value = loc.src.personalDistanceTo(loc.point.x, loc.point.y, disFunc, pixels.length, pixels[0].length);
		if(insert(pixels[loc.point.x][loc.point.y], value))//if one of the least, tell neighbors
		{
			for(int i = -1; i <= 1; i++)
			{
				for(int j = -1; j <= 1; j++)
				{
					if(i == 0 && j == 0) continue;//don't tell ourself...
					
					propagate(loc.point.x + i, loc.point.y + j, loc.src);//tell neighbor
				}
			}
		}
	}
	
	/**
	 * Flags the given location. Handles things like clipping the edges and not adding if a past value.
	 * @param x The x location of point.
	 * @param y The y location of point.
	 * @param src The src object.
	 */
	private void propagate(int x, int y, VoronoiObject src)
	{
		//clip values
		x = Util.clip(x, 0, pixels.length - 1);
		y = Util.clip(y, 0, pixels[0].length - 1);
		
		//create new location
		Location newLoc = new Location(new Point2D(x, y), src);
		//if not previously flagged
		if(!past.contains(newLoc))
		{
			//flag
			flagged.add(newLoc);
		}
	}
	
	/**
     * Inserts a value into an array so that the array is sorted from least to greatest.
     * If the value is greater than the max value, it is not added.
     * Returns true if the value was added to the array.
     * @param array The array to add to.
     * @param value The value we might add.
     * @return True if added, false if discarded.
     */
    private static final boolean insert(double[] array, double value)
    {
    	double temp;
        for(int i = array.length - 1; i >= 0; i--)
        {
            if(value > array[i]) 
            {
            	if(i == array.length - 1)
            	{
            		return false;
            	}
           	 	break;
            }
            temp = array[i];
            array[i] = value;
            if (i + 1 < array.length) 
            {
           	 	array[i + 1] = temp;
            }
        }
        return true;
    }
}
