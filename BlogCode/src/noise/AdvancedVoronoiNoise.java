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
public final class AdvancedVoronoiNoise extends Noise
{
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
	
	/**
	 * The list of Voronoi Objects to make the noise from.
	 */
	public ArrayList<VoronoiObject> objects;
	/**
	 * The distance function to use.
	 */
	public DistanceFunction disFunc;
	/**
	 * The combine function to use.
	 */
	public CombineFunction comFunc;
	/**
	 * Past calculated locations.
	 */
	private HashSet<Location> past;
	/**
	 * Locations currently being processed.
	 */
	private HashSet<Location> current;
	/**
	 * Locations flagged for next generation.
	 */
	private HashSet<Location> flagged;
	/**
	 * Distances for each pixel.
	 */
	private double[][][] pixels;
	
	/**
	 * Creates a new AdvancedVoronoiNoise for the given objects, distance function, and combine function.
	 * @param objs The Voronoi objects.
	 * @param dis The distance function to use.
	 * @param com The combine function to use.
	 */
	public AdvancedVoronoiNoise(ArrayList<VoronoiObject> objs, DistanceFunction dis, CombineFunction com)
	{
		objects = objs;
		disFunc = dis;
		comFunc = com;
	}
	
	@Override
	public void fillArray(NoiseArray noise)
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
		double value = loc.src.personalDistanceTo(loc.point.x, loc.point.y, disFunc);
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
    private boolean insert(double[] array, double value)
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
