package util;

public class Util
{
	/**
	 * Clips the given int to between the min and max.
	 * @param value The value to clip.
	 * @param min The min value.
	 * @param max The max value.
	 * @return The clipped value.
	 */
	public static final int clip(int value, int min, int max)
	{
		if(value < min)
		{
			return min;
		}else
		if(value > max)
		{
			return max;
		}else
		{
			return value;
		}
	}
	
	/**
	 * Wraps the given int to between the min and max.
	 * @param value The value to wrap.
	 * @param min The min value.
	 * @param max The max value.
	 * @return The wrapped value.
	 */
	public static final int wrap(int value, int min, int max)
	{
		int dif = max - min + 1;
		
		//naive implementation
		/*
		while(value < min)
		{
			value += dif;
		}
		while(value > max)
		{
			value -= dif;
		}
		*/
		
		//fast implementation
		value -= min;
		if(value < 0)//if less than 0 (aka % won't work)
		{
			value += ((-value / dif) + 1) * dif;//find exact amount needed to bring over 0
		}
		value %= dif;
		return value;
	}
}
