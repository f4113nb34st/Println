package util;

public class FastMath
{
	private static final double rev = Math.PI * 2;
	private static final double hrev = rev / 2;
	private static final double qrev = rev / 4;
	private static final int SAMPLES = 256;
	private static final double RESOLUTION = qrev / (SAMPLES - 1);
	private static final double INVRES = 1 / RESOLUTION;
	private static final double[] cos = new double[SAMPLES];
	static
	{
		for(int i = 0; i < SAMPLES; i++)
		{
			cos[i] = Math.cos(RESOLUTION * i);
		}
	}
	
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
	
	public static final double sin(double rads)
	{
		return cos(rads - qrev);
	}
	
	public static final double tan(double rads)
	{
		return sin(rads) / cos(rads);
	}
}
