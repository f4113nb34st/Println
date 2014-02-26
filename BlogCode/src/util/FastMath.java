package util;

/**
 * 
 * Stores functions for fast sin, cos, and tan approximations.
 * 
 * @author F4113nb34st
 *
 */
public class FastMath
{
	//one revolution
	private static final double rev = Math.PI * 2;
	//half rev
	private static final double hrev = rev / 2;
	//quarter rev
	private static final double qrev = rev / 4;
	//the sample size to use (higher uses more memory but is more precise)
	private static final int SAMPLES = 256;
	//the resolution of the samples
	private static final double RESOLUTION = qrev / (SAMPLES - 1);
	//inverse resolution
	private static final double INVRES = 1 / RESOLUTION;
	//list of cos values, all other functions can be calculated off these
	private static final double[] cos = new double[SAMPLES];
	//populate the cos array
	static
	{
		for(int i = 0; i < SAMPLES; i++)
		{
			cos[i] = Math.cos(RESOLUTION * i);
		}
	}
	
	/**
	 * Returns the cos value for the given radian measure.
	 * @param rads The radian measure.
	 * @return The cos value.
	 */
	public static final double cos(double rads)
	{
		if(rads < 0)//flip to >0
		{
			rads = -rads;
		}
		rads %= rev;//mod by 360
		if(rads > hrev)//if >180, map 360->0 and 180->180
		{
			rads = rev - rads;
		}
		if(rads <= qrev)//if <90
		{
			return cos[(int)(rads / INVRES)];
		}else
		{
			return -cos[(int)((hrev - rads) / INVRES)];
		}
	}
	
	/**
	 * Returns the sin value for the given radian measure.
	 * @param rads The radian measure.
	 * @return The sin value.
	 */
	public static final double sin(double rads)
	{
		//sin = cos - 90
		return cos(rads - qrev);
	}
	
	/**
	 * Returns the tan value for the given radian measure.
	 * @param rads The radian measure.
	 * @return The tan value.
	 */
	public static final double tan(double rads)
	{
		//tan = sin / cos
		return sin(rads) / cos(rads);
	}
}
