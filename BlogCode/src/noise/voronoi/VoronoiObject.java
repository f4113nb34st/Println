package noise.voronoi;

import java.util.HashSet;
import math.Point2D;

/**
 * 
 * Represents any object that can be used as a Voronoi diagram source object.
 * 
 * @author F4113nb34st
 *
 */
public abstract class VoronoiObject
{
	/**
	 * If this is not null, will be used instead of the given distance function.
	 */
	public DistanceFunction personalDisFunc = null;
	
	/**
	 * Gets distance from point to this object, possibly using the personal distance function.
	 * @param x The x coord.
	 * @param y The y coord.
	 * @param disFunc The default distance function to use.
	 * @return The distance to the point.
	 */
	public double personalDistanceTo(double x, double y, DistanceFunction disFunc)
	{
		//if we have a personal dis func
		if(personalDisFunc != null)
		{
			//usurp the default dis func
			disFunc = personalDisFunc;
		}
		//return the distance of the actual object
		return getDistanceTo(x, y, disFunc);
	}
	
	/**
	 * Returns the shortest distance from this object to the given point using the given distance function.
	 * @param x The x coord.
	 * @param y The y coord.
	 * @param disFunc The distance function to use.
	 * @return The distance to the point.
	 */
	public abstract double getDistanceTo(double x, double y, DistanceFunction disFunc);
	
	/**
	 * Adds the origins of this object to the given HashSet.
	 * @param centers The set to add to.
	 */
	public abstract void addCenters(HashSet<Point2D> centers);
}
