package core;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import core.base.NoiseDisplayCoreWeb;
import noise.PerlinNoise;

/**
 * 
 * Demonstrates the generation of Perlin Noise.
 * 
 * @author F4113nb34st
 *
 */
@SuppressWarnings("serial")
public class PerlinNoiseGenerationWeb extends NoiseDisplayCoreWeb
{
	private PerlinNoise noiseFunc = new PerlinNoise(0, 5, 5);
	
	@Override 
	public boolean key(int code)
	{
		switch(code)
		{
			case KeyEvent.VK_Q://if Q, increase x period
			{
				noiseFunc.periodX++;
				if(noiseFunc.periodX > WIDTH)
				{
					noiseFunc.periodX = WIDTH;
				}
				return true;
			}
			case KeyEvent.VK_E://if E, decrease x period
			{
				noiseFunc.periodX--;
				if(noiseFunc.periodX < 1)
				{
					noiseFunc.periodX = 1;
				}
				return true;
			}
			case KeyEvent.VK_A://if A, increase y period
			{
				noiseFunc.periodY++;
				if(noiseFunc.periodY > HEIGHT)
				{
					noiseFunc.periodY = HEIGHT;
				}
				return true;
			}
			case KeyEvent.VK_D://if D, decrease y period
			{
				noiseFunc.periodY--;
				if(noiseFunc.periodY < 1)
				{
					noiseFunc.periodY = 1;
				}
				return true;
			}
		}
		
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
		drawString("Period X: " + noiseFunc.periodX, 100, g2);
		drawString("Period Y: " + noiseFunc.periodY, 100, g2);
	}
}
