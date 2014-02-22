package noise.voronoi;

/**
 * Distance function to use in Voronoi calculations.
 * Euclid produces a scale pattern.
 * Manhattan and Chebychev are rectangle patterns.
 * Minkowski produces a star pattern.
 */
public enum DistanceFunction
{
	Euclid, Manhattan, Chebyshev, Minkowski0_5;
	
	/**
	 * Returns the distance between the given points.
	 * Note: depending on the function, may not be linear distance.
	 */
	public double distanceFunc(double x1, double y1, double x2, double y2)
	{
		switch(this)
		{
			default://default is also Euclid
				return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
			case Manhattan:	
				return Math.abs(x1 - x2) + Math.abs(y1 - y2);
			case Chebyshev:	
		        return Math.max(Math.abs(x1 - x2), Math.abs(y1 - y2));
			case Minkowski0_5:
				double x = Math.sqrt(Math.abs(x1 - x2));
				double y = Math.sqrt(Math.abs(y1 - y2));
				return (x+y) * (x+y);
		}
	}
}
