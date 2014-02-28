package core;

import util.concurrent.ThreadPool;
import noise.*;

/**
 * 
 * Tests the multi threaded generation of noise.
 * 
 * @author F4113nb34st
 *
 */
public class MultiThreadedingTest
{
	public static void main(String[] args)
	{
		try
		{
			//the number of tests to run
			final int tests = 10;
			//the total time for the defaults
			long defTime = 0;
			//the total time for multithreadeds
			long mtTime = 0;
			
			long time;
			for(int i = 0; i < tests; i++)
			{
				//print tests number
				System.out.println("Test " + i);
				
				//generate noise using default method, keeping track of time
				time = System.currentTimeMillis();
				noiseFunc.fillArray(noise);
				defTime += (System.currentTimeMillis() - time);
				
				//generate noise multi-threadedly, keeping track of time
				time = System.currentTimeMillis();
				((MultiThreadedNoise)noiseFunc).fillMultiThreaded(noise, pool);
				mtTime += (System.currentTimeMillis() - time);
			}
			
			//get averages
			defTime /= tests;
			mtTime /= tests;
			
			//print results
			System.out.println("Average Default Time: " + defTime);
			System.out.println("Average Multi-Threaded Time: " + mtTime);
			System.out.println("Average Speed Increase: " + ((defTime * 10 / mtTime) / 10D) + "x");
			
		}catch(Exception ex)//catch any exceptions
		{
			ex.printStackTrace();
		}
	}
	
	public static final NoiseArray noise = new NoiseArray(1024, 1024);
	public static final ThreadPool pool = new ThreadPool(Runtime.getRuntime().availableProcessors());
	public static final Noise noiseFunc = new FractalNoise(0, new PerlinNoise(), 4, 8, .5);
}
