package noise.voronoi;

/**
 * Distance function to use in Voronoi calculations.
 * Euclid and EuclidSq produces a scale pattern.
 * Manhattan and Chebychev are rectangle patterns.
 * Minkowski produces a star pattern.
 */
public enum DistanceFunction
{
	/**"Real" distance. Produces a scale-like pattern.*/
	Euclid, 
	/**Produces a dark scale-like pattern.*/
	EuclidSq, 
	/**Produces a square pattern.*/
	Manhattan, 
	/**Produces a diamond pattern.*/
	Chebyshev, 
	/**Produces a star pattern.*/
	Minkowski0_5;
	
	/**
	 * Returns the distance between the given points.
	 * Note: depending on the function, may not be linear distance.
	 */
	public double distanceFunc(double x1, double y1, double x2, double y2)
	{
		switch(this)
		{
			default://default is also Euclid
			case EuclidSq:
				return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);//returns the square distance, sqrt's are calculated later for Euclidean distance.
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
