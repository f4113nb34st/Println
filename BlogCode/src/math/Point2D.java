package math;

/**
 * 
 * Simple yet comprehensive class that represents a 2D point.
 * 
 * @author F4113nb34st
 *
 */
public final class Point2D
{
	public int x;
	public int y;
	
	/**
	 * Creates a new point at (0,0).
	 */
	public Point2D()
	{
		set(0, 0);
	}
	
	/**
	 * Creates a new point at (i,j).
	 * @param i The x coord of this point.
	 * @param j The y coord of this point.
	 */
	public Point2D(int i, int j)
	{
		set(i, j);
	}
	
	/**
	 * Creates a new point equal to the given vetor.
	 * @param vec The point to set this new one to.
	 */
	public Point2D(Point2D vec)
	{
		set(vec);
	}
	
	@Override
	public String toString()
	{
		return "Point2D (" + x + ", " + y + ")";
	}
	
	/**
	 * Overrides the default equals to accept equal but separate objects.
	 * @param The object to compare to.
	 * @return True if the points are equal.
	 */
	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof Point2D))
		{
			return false;
		}
		return hashCode() == obj.hashCode();
	}
	
	/**
	 * Hashes this point so the hash is based on the values of x and y.
	 * @return The hash of this point.
	 */
	@Override
	public int hashCode()
	{
		int multi = 451584;
		int value = 0;
		value = (value * multi) + x;
		value = (value * multi) + y;
	    return value;
	}
	
	/**
	 * Returns a copy of this point.
	 * @return The copy of this point.
	 */
	public Point2D copy()
	{
		return new Point2D(this);
	}
	
	/**
	 * Sets this point to i, j, and returns itself.
	 * @param i The x coord to set to.
	 * @param j The y coord to set to.
	 * @return This point.
	 */
	public Point2D set(int i, int j)
	{
		x = i;
		y = j;
		return this;
	}
	
	/**
	 * Sets this point to the given point, and returns itself.
	 * @param point The point to set to.
	 * @return This point.
	 */
	public Point2D set(Point2D point)
	{
		x = point.x;
		y = point.y;
		return this;
	}
	
	/**
	 * Negates this point and returns itself.
	 * @return This point.
	 */
	public Point2D negate()
	{
		x = -x;
		y = -y;
		return this;
	}
	
	/**
	 * Returns the length of this point squared.
	 * @return The squared length.
	 */
	public double lengthSq()
	{
		return (x * x) + (y * y);
	}
	
	/**
	 * Returns the length of this point.
	 * @return The length.
	 */
	public double length()
	{
		return Math.sqrt(lengthSq());
	}
	
	/**
	 * Adds the given ints to this point and returns itself.
	 * @param xincrement The int to add to x.
	 * @param yincrement The int to add to y.
	 * @return This point.
	 */
	public Point2D add(int xincrement, int yincrement)
	{
		x += xincrement;
		y += yincrement;
		return this;
	}
	
	/**
	 * Adds the given point to this point and returns itself.
	 * @param increment The point to add.
	 * @return This point.
	 */
	public Point2D add(Point2D increment)
	{
		add(increment.x, increment.y);
		return this;
	}
	
	/**
	 * Subtracts the given ints from this point and returns itself.
	 * @param xincrement The int to subtract from x.
	 * @param yincrement The int to subtract from y.
	 * @return This point.
	 */
	public Point2D subtract(int xincrement, int yincrement)
	{
		return add(-xincrement, -yincrement);
	}
	
	/**
	 * Subtracts the given point from this point and returns itself.
	 * @param increment The point to subtract
	 * @return This point.
	 */
	public Point2D subtract(Point2D increment)
	{
		subtract(increment.x, increment.y);
		return this;
	}
	
	/**
	 * Multiplies this point by the given int and returns itself.
	 * @param multi The int to multiply by.
	 * @return The resulting point.
	 */
	public Point2D multiply(int multi)
	{
		x *= multi;
		y *= multi;
		return this;
	}
	
	/**
	 * Multiplies this point by the given point and returns itself.
	 * @param multi The point to multiply by.
	 * @return This point.
	 */
	public Point2D multiply(Point2D multi)
	{
		x *= multi.x;
		y *= multi.y;
		return this;
	}
	
	@SuppressWarnings("serial")
	private static final class DivideByZeroException extends RuntimeException{}
	
	/**
	 * Divides this point by the given int and returns itself.
	 * @param multi The int to divide by.
	 * @return This point.
	 */
	public Point2D divide(int multi)
	{
		if(multi == 0)
		{
			System.out.println("[Point2D] Divide by zero!");
			throw new DivideByZeroException();
		}
		x /= multi;
		y /= multi;
		return this;
	}
	
	/**
	 * Divides this point by the given point and returns itself.
	 * @param multi The point to divide by.
	 * @return This point.
	 */
	public Point2D divide(Point2D multi)
	{
		if(multi.x == 0 || multi.y == 0)
		{
			System.out.println("[Point2D] Divide by zero!");
			throw new DivideByZeroException();
		}
		x /= multi.x;
		y /= multi.y;
		return this;
	}
	
	/**
	 * Returns the distance squared between the two given points.
	 * @param v1 Vector 1.
	 * @param v2 Vector 2.
	 * @return The squared distance.
	 */
	public static final double distanceSq(Point2D v1, Point2D v2)
	{
		int xdif = v2.x - v1.x;
		int ydif = v2.y - v1.x;
		
		return (xdif * xdif) + (ydif * ydif);
	}
	
	/**
	 * Returns the distance between the two given points.
	 * @param v1 Vector 1.
	 * @param v2 Vector 2.
	 * @return The distance.
	 */
	public static final double distance(Point2D v1, Point2D v2)
	{
		return Math.sqrt(distanceSq(v1, v2));
	}
}
