package util;

public class FrameRate
{
	//stuff for calculating frame rate
	private static int frames;
	private static long start;
	private static float framerate;
	private static int frameRateCalcRate = 1000;
	
	/**
	 * Call every time a frame happens.
	 */
	public static void poll()
	{
		frames++;
		long time = System.currentTimeMillis();
		if(time > start + frameRateCalcRate)
		{
			calcFrameRate();
			frames = 0;
			start = time;
		}
	}
	
	/**
	 * Returns the current frame rate.
	 */
	public static float getFrameRate()
	{
		return framerate;
	}
	
	/**
	 * Sets the framerate variable.
	 */
	private static void calcFrameRate()
	{
		framerate = (int)(frames / ((System.currentTimeMillis() - start) / 1000F));
	}
}
