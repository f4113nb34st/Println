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
		int hx = xsize / 2;
		int hy = ysize / 2;
		if(Math.abs(i - x) > hx)
		{
			if(i > x)
			{
				i -= xsize;
			}else
			{
				i += xsize;
			}
		}
		if(Math.abs(j - y) > hy)
		{
			if(j > y)
			{
				j -= ysize;
			}else
			{
				j += ysize;
			}
		}
		return disFunc.distanceFunc(x, y, i, j);
	}
	
	public void addCenters(HashSet<Point2D> centers)
	{
		centers.add(new Point2D(x, y));
	}
}
