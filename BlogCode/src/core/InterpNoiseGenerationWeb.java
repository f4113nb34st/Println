package core;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import core.base.NoiseDisplayCoreWeb;
import util.Interpolation;
import noise.InterpNoise;

/**
 * 
 * Demonstrates the generation of Interpolated Noise.
 * 
 * @author F4113nb34st
 *
 */
@SuppressWarnings("serial")
public class InterpNoiseGenerationWeb extends NoiseDisplayCoreWeb
{		
	//the interp noise generator
	private InterpNoise noiseFunc = new InterpNoise(0, 5, 5, Interpolation.LINEAR);
	
	@Override 
	public boolean key(int code)
	{
		switch(code)
		{
			case KeyEvent.VK_SPACE://if space, go to next interpolation type
			{
				int index = noiseFunc.interp.ordinal();
				index++;
				index %= Interpolation.values().length;
				noiseFunc.interp = Interpolation.values()[index];
				return true;
			}
			case KeyEvent.VK_Q://if Q, increase periodX
			{
				noiseFunc.periodX++;
				if(noiseFunc.periodX > WIDTH)
				{
					noiseFunc.periodX = WIDTH;
				}
				return true;
			}
			case KeyEvent.VK_E://if E, decrease periodX
			{
				noiseFunc.periodX--;
				if(noiseFunc.periodX < 1)
				{
					noiseFunc.periodX = 1;
				}
				return true;
			}
			case KeyEvent.VK_A://if A, increase periodY
			{
				noiseFunc.periodY++;
				if(noiseFunc.periodY > HEIGHT)
				{
					noiseFunc.periodY = HEIGHT;
				}
				return true;
			}
			case KeyEvent.VK_D://if D, decrease periodY
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
		drawString(noiseFunc.interp.getName(), 100, g2);
		drawString("Period X: " + noiseFunc.periodX, 100, g2);
		drawString("Period Y: " + noiseFunc.periodY, 100, g2);
	}
}
