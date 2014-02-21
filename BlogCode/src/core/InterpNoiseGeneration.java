package core;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import core.base.NoiseDisplayCore;
import util.Interpolation;
import noise.InterpNoise;

/**
 * 
 * Demonstrates the generation of Interpolated Noise.
 * 
 * @author F4113nb34st
 *
 */
public class InterpNoiseGeneration extends NoiseDisplayCore implements KeyListener
{
	public static void main(String[] args)
	{
		try
		{
			InterpNoiseGeneration main = new InterpNoiseGeneration("Interpolated Noise");
			main.init();
			main.renderLoop();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
			
	private Interpolation inter = Interpolation.LINEAR;
	private int periodX = 5;
	private int periodY = 5;
	
	public InterpNoiseGeneration(String title)
	{
		super(title);
	}
	
	@Override 
	public boolean key(int code)
	{
		switch(code)
		{
			case KeyEvent.VK_SPACE://if space, go to next interpolation type
			{
				int index = inter.ordinal();
				index++;
				index %= Interpolation.values().length;
				inter = Interpolation.values()[index];
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
		InterpNoise.fill_interp_noise_array(noise, null, seed, inter, periodX, periodY);
	}

	@Override
	public void drawInfo(Graphics2D g2)
	{
		drawString(inter.getName(), 100, g2);
		drawString("Period X: " + periodX, 100, g2);
		drawString("Period Y: " + periodY, 100, g2);
	}
}
