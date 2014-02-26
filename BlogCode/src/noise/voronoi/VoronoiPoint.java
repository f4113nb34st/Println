package noise.voronoi;

import java.util.HashSet;
import math.Point2D;

public class VoronoiPoint extends VoronoiObject
{
	public int x;
	public int y;
	
	public VoronoiPoint(int i, int j)
	{
		x = i;
		y = j;
	}

	@Override
	public double getDistanceTo(double i, double j, DistanceFunction disFunc)
	{
		return disFunc.distanceFunc(x, y, i, j);
	}
	
	public void addCenters(HashSet<Point2D> centers)
	{
		centers.add(new Point2D(x, y));
	}
}
