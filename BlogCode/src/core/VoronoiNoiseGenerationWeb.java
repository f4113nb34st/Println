package core;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import core.base.NoiseDisplayCoreWeb;
import noise.VoronoiNoise;
import noise.voronoi.CombineFunction;
import noise.voronoi.DistanceFunction;

/**
 * 
 * Demonstrates the generation of Voronoi Noise.
 * 
 * @author F4113nb34st
 *
 */
@SuppressWarnings("serial")
public class VoronoiNoiseGenerationWeb extends NoiseDisplayCoreWeb
{
	//the voronoi noise generator
	public VoronoiNoise noiseFunc = new VoronoiNoise(0, 64, 64, DistanceFunction.Euclid, CombineFunction.F1);
	
	@Override 
	public boolean key(int code)
	{
		switch(code)
		{
			case KeyEvent.VK_SPACE://if SPACE, next distance function
			{
				int index = noiseFunc.disFunc.ordinal();
				index++;
				index %= DistanceFunction.values().length;
				noiseFunc.disFunc = DistanceFunction.values()[index];
				return true;
			}
			case KeyEvent.VK_X://if X, next combine function
			{
				int index = noiseFunc.comFunc.ordinal();
				index++;
				index %= CombineFunction.values().length;
				noiseFunc.comFunc = CombineFunction.values()[index];
				return true;
			}
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
		drawString("Dis Func: " + noiseFunc.disFunc.name(), 175, g2);
		drawString("Com Func: " + noiseFunc.comFunc.name(), 175, g2);
		drawString("Period X: " + noiseFunc.periodX, 175, g2);
		drawString("Period Y: " + noiseFunc.periodY, 175, g2);
	}
}
