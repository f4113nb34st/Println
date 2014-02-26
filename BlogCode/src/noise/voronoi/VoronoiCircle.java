package noise.voronoi;

import java.util.HashSet;
import util.FastMath;
import math.Point2D;

/**
 * 
 * VoronoiObject that represents a circle.
 * 
 * @author F4113nb34st
 *
 */
public class VoronoiCircle extends VoronoiObject
{
	/**
	 * The center of this circle.
	 */
	public Point2D center;
	/**
	 * The radius of this circle.
	 */
	public double radius;
	/**
	 * True if filled, false if ring.
	 */
	public boolean filled;
	
	/**
	 * Creates a new VoronoiCircle with the given center, radius, and fill.
	 * @param cx The x coord of the center.
	 * @param cy The y coord of the center.
	 * @param r The radius.
	 * @param fill True if filled, false if ring.
	 */
	public VoronoiCircle(int cx, int cy, double r, boolean fill)
	{
		this(new Point2D(cx, cy), r, fill);
	}
	
	/**
	 * Creates a new VoronoiCircle with the given center, radius, and fill.
	 * @param c The center point.
	 * @param r The radius.
	 * @param fill True if filled, false if ring.
	 */
	public VoronoiCircle(Point2D c, double r, boolean fill)
	{
		center = c;
		radius = r;
		filled = fill;
	}

	@Override
	public double getDistanceTo(double i, double j, DistanceFunction disFunc)
	{
		//get distance to center
		double dis = disFunc.distanceFunc(center.x, center.y, i, j);
		//if Euclidean distance
		if(disFunc == DistanceFunction.Euclid || disFunc == DistanceFunction.EuclidSq)
		{
			//subtract radiusSq
			dis -= radius * radius;
		}else
		{
			//subtract radius
			dis -= radius;
		}
		//if inside circle
		if(dis < 0)
		{
			//if filled, clip to 0
			if(filled)
			{
				dis = 0;
			}else//invert to positive
			{
				dis = -dis;
			}
		}
		//return the distance
		return dis;
	}

	@Override
	public void addCenters(HashSet<Point2D> centers)
	{
		//stores previous x and y values
		int px = Integer.MAX_VALUE;
		int py = Integer.MAX_VALUE;
		//the circumference of the circle
		double circum = Math.PI * radius * radius;
		//for each point on edge
		for(double theta = 0; theta < (Math.PI * 2); theta += (Math.PI * 2 / circum))
		{
			//find x and y values
			int x = (int)Math.round(center.x + (FastMath.cos(theta) * radius));
			int y = (int)Math.round(center.y + (FastMath.sin(theta) * radius));
			//no sense re-adding previous value
			if(x == px && y == py)
			{
				continue;
			}
			//add center
			centers.add(new Point2D(x, y));
			//set previouses
			px = x;
			py = y;
		}
	}
}
