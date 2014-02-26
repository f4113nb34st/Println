package util;

/**
 * 
 * Simple class for storing and measuring frame rate.
 * 
 * @author F4113nb34st
 *
 */
public class FrameRate
{
	//the frames since last calc
	private static int frames;
	//the last frame calc time
	private static long start;
	//current frame rate
	private static float framerate;
	//rate to recalc frame rate
	private static int frameRateCalcRate = 1000;
	
	/**
	 * Call every time a frame is rendered.
	 */
	public static void poll()
	{
		//inc frames
		frames++;
		long time = System.currentTimeMillis();
		//if time to reclc
		if(time > start + frameRateCalcRate)
		{
			//recalc
			calcFrameRate();
			//reset frames
			frames = 0;
			//set start time
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
		framerate = frames / ((System.currentTimeMillis() - start) / 1000F);
		if(framerate > 5)//if >5 round to nearest frame
		{
			framerate = Math.round(framerate);
		}
	}
}
