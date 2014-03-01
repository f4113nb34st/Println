package core;

import java.awt.Graphics2D;
import core.base.NoiseDisplayCore;
import noise.MidDisNoise;

/**
 * 
 * Demonstrates the generation of Midpoint Displacement Noise.
 * 
 * @author F4113nb34st
 *
 */
public class MidDisNoiseGeneration extends NoiseDisplayCore
{
	public static void main(String[] args)
	{
		try
		{
			//general init stuff
			MidDisNoiseGeneration main = new MidDisNoiseGeneration("Midpoint Displacement Noise");
			main.init();
			main.renderLoop();
		}catch(Exception ex)//catch any exceptions
		{
			ex.printStackTrace();
		}
	}
			
	//the interp noise generator
	private MidDisNoise noiseFunc = new MidDisNoise(0, 1, .5, false);
	
	//pass title to SimpleCore
	public MidDisNoiseGeneration(String title)
	{
		super(title);
	}
	
	@Override 
	public boolean key(int code)
	{
		return false;
	}
	
	@Override
	public void regenNoise(long seed)
	{
		//update seed
		noiseFunc.seed = seed;
		//fill array
		noiseFunc.fillArray(noise);
	}

	@Override
	public void drawInfo(Graphics2D g2)
	{
		//display infos
	}
}
