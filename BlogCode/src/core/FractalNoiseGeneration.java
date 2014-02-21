package core;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import core.base.NoiseDisplayCore;
import util.Interpolation;
import noise.FractalNoise;
import noise.InterpNoise;

/**
 * 
 * Demonstrates the generation of Fractal Noise.
 * 
 * @author F4113nb34st
 *
 */
public class FractalNoiseGeneration extends NoiseDisplayCore implements KeyListener
{
	public static void main(String[] args)
	{
		try
		{
			FractalNoiseGeneration main = new FractalNoiseGeneration("Fractal Noise");
			main.init();
			main.renderLoop();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
			
	private Interpolation inter = Interpolation.LINEAR;
	private int fineOctave = 0;
	private int broadOctave = 8;
	private double persistence = .5;
	
	public FractalNoiseGeneration(String title)
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
			case KeyEvent.VK_ENTER://if enter, save to desktop
			{
				takeScreenShot();
				break;
			}
			case KeyEvent.VK_Q:
			{
				if(fineOctave < broadOctave)
				{
					fineOctave++;
					return true;
				}
				break;
			}
			case KeyEvent.VK_E:
			{
				if(fineOctave > 0)
				{
					fineOctave--;
					return true;
				}
				break;
			}
			case KeyEvent.VK_A:
			{
				if((1 << broadOctave) < Math.min(WIDTH, HEIGHT))
				{
					broadOctave++;
					return true;
				}
				break;
			}
			case KeyEvent.VK_D:
			{
				if(broadOctave > fineOctave)
				{
					broadOctave--;
					return true;
				}
				break;
			}
			case KeyEvent.VK_Z:
			{
				if(persistence < 2)
				{
					persistence += .1;
					return true;
				}
				break;
			}
			case KeyEvent.VK_C:
			{
				if(persistence > 0)
				{
					persistence -= .1;
					return true;
				}
				break;
			}
		}
		persistence = Math.round(persistence * 10) / 10D;
		
		return false;
	}
	
	@Override
	public void regenNoise(long seed)
	{
		FractalNoise.fill_fractal_noise_array(noise, seed, InterpNoise.getAsFunction(inter), fineOctave, broadOctave, persistence);
	}

	@Override
	public void drawInfo(Graphics2D g2)
	{
		drawString(inter.getName(), 125, g2);
		drawString("Fine Octave: " + fineOctave, 125, g2);
		drawString("Broad Octave: " + broadOctave, 125, g2);
		drawString("Persistence: " + persistence, 125, g2);
	}
}
