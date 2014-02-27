package core;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
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
public class FractalNoiseGeneration extends NoiseDisplayCore
{
	public static void main(String[] args)
	{
		try
		{
			//general init stuff
			FractalNoiseGeneration main = new FractalNoiseGeneration("Fractal Noise");
			main.init();
			main.renderLoop();
		}catch(Exception ex)//catch any exceptions
		{
			ex.printStackTrace();
		}
	}
	
	//the base noise function
	private InterpNoise baseFunc = new InterpNoise(Interpolation.LINEAR);
	//the fractal noise function
	private FractalNoise noiseFunc = new FractalNoise(0, baseFunc, 0, 8, .5);
	
	//pass title to SimpleCore
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
				int index = baseFunc.interp.ordinal();
				index++;
				index %= Interpolation.values().length;
				baseFunc.interp = Interpolation.values()[index];
				return true;
			}
			case KeyEvent.VK_ENTER://if enter, save to desktop
			{
				takeScreenShot();
				break;
			}
			case KeyEvent.VK_Q://if Q increase fineOctave
			{
				if(noiseFunc.fineOctave < noiseFunc.broadOctave)
				{
					noiseFunc.fineOctave++;
					return true;
				}
				break;
			}
			case KeyEvent.VK_E://if E decrease fineOctave
			{
				if(noiseFunc.fineOctave > 0)
				{
					noiseFunc.fineOctave--;
					return true;
				}
				break;
			}
			case KeyEvent.VK_A://if A increase broadOctave
			{
				if((1 << noiseFunc.broadOctave) < Math.min(WIDTH, HEIGHT))
				{
					noiseFunc.broadOctave++;
					return true;
				}
				break;
			}
			case KeyEvent.VK_D://if D decrease broadOctave
			{
				if(noiseFunc.broadOctave > noiseFunc.fineOctave)
				{
					noiseFunc.broadOctave--;
					return true;
				}
				break;
			}
			case KeyEvent.VK_Z://if Z increase persistence
			{
				if(noiseFunc.persistence < 2)
				{
					noiseFunc.persistence += .1;
					return true;
				}
				break;
			}
			case KeyEvent.VK_C://if C decrease persistence
			{
				if(noiseFunc.persistence > 0)
				{
					noiseFunc.persistence -= .1;
					return true;
				}
				break;
			}
		}
		//round persistence
		noiseFunc.persistence = Math.round(noiseFunc.persistence * 10) / 10D;
		
		return false;
	}
	
	@Override
	public void regenNoise(long seed)
	{
		//update seed
		noiseFunc.seed = seed;
		//fill noise
		noiseFunc.fillArray(noise);
	}

	@Override
	public void drawInfo(Graphics2D g2)
	{
		//display infos
		drawString(baseFunc.interp.getName(), 125, g2);
		drawString("Fine Octave: " + noiseFunc.fineOctave, 125, g2);
		drawString("Broad Octave: " + noiseFunc.broadOctave, 125, g2);
		drawString("Persistence: " + noiseFunc.persistence, 125, g2);
	}
}
