package core;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import core.base.NoiseDisplayCore;
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
public class VoronoiNoiseGeneration extends NoiseDisplayCore implements KeyListener
{
	public static void main(String[] args)
	{
		try
		{
			VoronoiNoiseGeneration main = new VoronoiNoiseGeneration("Voronoi Noise");
			main.init();
			main.renderLoop();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	private DistanceFunction disFunc = DistanceFunction.Euclid;
	private CombineFunction comFunc = CombineFunction.F1;
	private int periodX = 64;
	private int periodY = 64;
	
	public VoronoiNoiseGeneration(String title)
	{
		super(title);
	}
	
	@Override 
	public boolean key(int code)
	{
		switch(code)
		{
			case KeyEvent.VK_SPACE:
			{
				int index = disFunc.ordinal();
				index++;
				index %= DistanceFunction.values().length;
				disFunc = DistanceFunction.values()[index];
				return true;
			}
			case KeyEvent.VK_X:
			{
				int index = comFunc.ordinal();
				index++;
				index %= CombineFunction.values().length;
				comFunc = CombineFunction.values()[index];
				return true;
			}
			case KeyEvent.VK_Q:
			{
				periodX++;
				if(periodX > WIDTH)
				{
					periodX = WIDTH;
				}
				return true;
			}
			case KeyEvent.VK_E:
			{
				periodX--;
				if(periodX < 1)
				{
					periodX = 1;
				}
				return true;
			}
			case KeyEvent.VK_A:
			{
				periodY++;
				if(periodY > HEIGHT)
				{
					periodY = HEIGHT;
				}
				return true;
			}
			case KeyEvent.VK_D:
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
		long time = System.currentTimeMillis();
		VoronoiNoise.fill_voronoi_noise_array(noise, seed, periodX, periodY, disFunc, comFunc);
		System.out.println(System.currentTimeMillis() - time);
	}

	@Override
	public void drawInfo(Graphics2D g2)
	{
		drawString("Period X: " + periodX, 100, g2);
		drawString("Period Y: " + periodY, 100, g2);
	}
}
