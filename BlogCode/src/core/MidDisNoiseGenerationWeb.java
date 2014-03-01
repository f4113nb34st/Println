package core;

import java.awt.Graphics2D;
import core.base.NoiseDisplayCoreWeb;
import noise.MidDisNoise;

/**
 * 
 * Demonstrates the generation of Midpoint Displacement Noise.
 * 
 * @author F4113nb34st
 *
 */
@SuppressWarnings("serial")
public class MidDisNoiseGenerationWeb extends NoiseDisplayCoreWeb
{	
	//the interp noise generator
	private MidDisNoise noiseFunc = new MidDisNoise(0, 1, .5, false);
	
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
