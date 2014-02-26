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
	//the current distance function
	private DistanceFunction disFunc = DistanceFunction.Euclid;
	//the current combine function
	private CombineFunction comFunc = CombineFunction.F1;
	//the x period
	private int periodX = 64;
	//the y period
	private int periodY = 64;
	
	@Override 
	public boolean key(int code)
	{
		switch(code)
		{
			case KeyEvent.VK_SPACE://if SPACE, next distance function
			{
				int index = disFunc.ordinal();
				index++;
				index %= DistanceFunction.values().length;
				disFunc = DistanceFunction.values()[index];
				return true;
			}
			case KeyEvent.VK_X://if X, next combine function
			{
				int index = comFunc.ordinal();
				index++;
				index %= CombineFunction.values().length;
				comFunc = CombineFunction.values()[index];
				return true;
			}
			case KeyEvent.VK_Q://if Q, increase x period
			{
				periodX++;
				if(periodX > WIDTH)
				{
					periodX = WIDTH;
				}
				return true;
			}
			case KeyEvent.VK_E://if E, decrease x period
			{
				periodX--;
				if(periodX < 1)
				{
					periodX = 1;
				}
				return true;
			}
			case KeyEvent.VK_A://if A, increase y period
			{
				periodY++;
				if(periodY > HEIGHT)
				{
					periodY = HEIGHT;
				}
				return true;
			}
			case KeyEvent.VK_D://if D, decrease y period
			{
				periodY--;
				if(periodY < 1)
				{
					periodY = 1;
				}
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void regenNoise(long seed)
	{
		//fill array
		VoronoiNoise.fill_voronoi_noise_array(noise, seed, periodX, periodY, disFunc, comFunc);
	}

	@Override
	public void drawInfo(Graphics2D g2)
	{
		//display infos
		drawString("Dis Func: " + disFunc.name(), 175, g2);
		drawString("Com Func: " + comFunc.name(), 175, g2);
		drawString("Period X: " + periodX, 175, g2);
		drawString("Period Y: " + periodY, 175, g2);
	}
}
