package core;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import core.base.NoiseDisplayCore;
import noise.PerlinNoise;

/**
 * 
 * Demonstrates the generation of Perlin Noise.
 * 
 * @author F4113nb34st
 *
 */
public class PerlinNoiseGeneration extends NoiseDisplayCore implements KeyListener
{
	public static void main(String[] args)
	{
		try
		{
			PerlinNoiseGeneration main = new PerlinNoiseGeneration("Perlin Noise");
			main.init();
			main.renderLoop();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	private int periodX = 5;
	private int periodY = 5;
	
	public PerlinNoiseGeneration(String title)
	{
		super(title);
	}
	
	@Override 
	public boolean key(int code)
	{
		switch(code)
		{
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
		PerlinNoise.fill_perlin_noise_array(noise, seed, periodX, periodY);
	}

	@Override
	public void drawInfo(Graphics2D g2)
	{
		drawString("Period X: " + periodX, 100, g2);
		drawString("Period Y: " + periodY, 100, g2);
	}
}
