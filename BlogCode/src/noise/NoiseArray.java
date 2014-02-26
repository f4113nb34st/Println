package noise;

import java.util.Arrays;
import util.Util;

/**
 * 
 * Wrapper class for a 2D double array that handles many convenient functions.
 * 
 * @author F4113nb34st
 *
 */
public class NoiseArray
{
	/**The base noise array of this NoiseArray. In most case you should not need to access this.*/
	public double[][] noise;
	//self explanatory
	public int minX;
	public int minY;
	public int maxX;
	public int maxY;
	//true if we should wrap x values
	public boolean wrapX = true;
	//true if we should wrap y values
	public boolean wrapY = true;
	
	/**Amplitude to multiply incoming values by.*/
	public double amplitude = 1;
	/**Offset to add to incoming values.*/
	public double offset = 0;
	
	/**
	 * Creates a new NoiseArray with the given with and height.
	 * @param w The width.
	 * @param h The height.
	 */
	public NoiseArray(int w, int h)
	{
		this(new double[w][h], w, h);
	}
	
	/**
	 * Creates a new wrapper NoiseArray around the given double array.
	 * @param n The double array to encapsulate.
	 */
	public NoiseArray(double[][] n)
	{
		this(n, n.length, n[0].length);
	}
	
	/**
	 * Creates a new wrapper NoiseArray around the given double array with the given width and height.
	 * @param n The double array to encapsulate.
	 * @param w The width.
	 * @param h The height.
	 */
	public NoiseArray(double[][] n, int w, int h)
	{
		noise = n;
		setBounds(w, h);
	}
	
	/**
	 * Creates a new wrapper NoiseArray around the given double array with the given mins and maxs.
	 * @param n The double array to encapsulate.
	 * @param miX The min x value.
	 * @param miY The min y value.
	 * @param maX The max x value.
	 * @param maY The max y value.
	 */
	public NoiseArray(double[][] n, int miX, int miY, int maX, int maY)
	{
		noise = n;
		setBounds(miX, miY, maX, maY);
	}
	
	/**
	 * Sets the bounds of this NoiseArray to the given values.
	 * @param miX The min x value.
	 * @param miY The min y value.
	 * @param maX The max x value.
	 * @param maY The max y value.
	 */
	public void setBounds(int miX, int miY, int maX, int maY)
	{
		minX = miX;
		minY = miY;
		maxX = maX;
		maxY = maY;
		ensureCapacity();
	}
	
	/**
	 * Sets the sizes of this NoiseArray to the given values.
	 * @param w The width to set to.
	 * @param h The height to set to.
	 */
	public void setBounds(int w, int h)
	{
		minX = 0;
		minY = 0;
		maxX = w - 1;
		maxY = h - 1;
		ensureCapacity();
	}
	
	/**
	 * Ensures there is enough capacity in this NoiseArray for its width and height.
	 */
	private void ensureCapacity()
	{
		if((maxX - minX) >= noise.length || (maxY - minY) >= noise[0].length)
		{
			noise = new double[maxX - minX + 1][maxY - minY + 1];
		}
	}
	
	/**
	 * Returns the width of this NoiseArray.
	 * @return The width.
	 */
	public int getWidth()
	{
		return maxX + 1 - minX;
	}
	
	/**
	 * Returns the height of this NoiseArray.
	 * @return The height.
	 */
	public int getHeight()
	{
		return maxY + 1 - minY;
	}
	
	/**
	 * Sets the given position to the given values.
	 * Ensures the values are within the bounds.
	 * @param x The x position.
	 * @param y The y position.
	 * @param value The value to set to.
	 */
	public void set(int x, int y, double value)
	{
		x = fix(x, minX, maxX, wrapX);
		y = fix(y, minY, maxY, wrapY);
		noise[x][y] = (value * amplitude) + offset;
	}
	
	/**
	 * Sets the given position relative to this NoiseArray's mins to the given values.
	 * Ensures the values are within the bounds.
	 * @param x The x position relative to minX.
	 * @param y The y position relative to minY.
	 * @param value The value to set to.
	 */
	public void setRelative(int x, int y, double value)
	{
		set(x + minX, y + minY, value);
	}
	
	/**
	 * Returns the value at the given position.
	 * Ensures the values are within the bounds.
	 * @param x The x position.
	 * @param y The y position.
	 * @return The value at the position.
	 */
	public double get(int x, int y)
	{
		x = fix(x, minX, maxX, wrapX);
		y = fix(y, minY, maxY, wrapY);
		return noise[x][y];
	}
	
	/**
	 * Returns the value at the given position relative to this NoiseArray's mins.
	 * Ensures the values are within the bounds.
	 * @param x The x position relative to minX.
	 * @param y The y position relative to minY.
	 * @return The value at the position.
	 */
	public double getRelative(int x, int y)
	{
		return get(x - minX, y - minY);
	}
	
	/**
	 * Normalizes the values of this noise array.
	 * (resizes so the max = 1 and min = 0)
	 */
	public void normalize()
	{
		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;
		
		//for all values, find min and max
		for(int x = minX; x <= maxX; x++)
		{
			for(int y = minY; y <= maxY; y++)
			{
				min = Math.min(min, noise[x][y]);
				max = Math.max(max, noise[x][y]);
			}
		}
		
		//find multi
		double multi = 1 / (max - min);
		
		//multiply all values by multi.
		for(int x = minX; x <= maxX; x++)
		{
			for(int y = minY; y <= maxY; y++)
			{
				noise[x][y] = ((noise[x][y] - min) * multi);
			}
		}
	}
	
	/**
	 * Fills this NoiseArray with the given value.
	 * @param value The value to fill with.
	 */
	public void fillWith(double value)
	{
		for(int x = minX; x <= maxX; x++)
		{
			Arrays.fill(noise[x], value);
		}
	}
	
	/**
	 * Ensures the given value is within min and max using the given flag to wrap or not.
	 * @param val The value to fix.
	 * @param min The min possible value.
	 * @param max The max possible value.
	 * @param wrap True to wrap, false to clip.
	 * @return The fixed value.
	 */
	private static final int fix(int val, int min, int max, boolean wrap)
	{
		if(wrap)
		{
			return Util.wrap(val, min, max);
		}else
		{
			return Util.clip(val, min, max);
		}
	}
}
