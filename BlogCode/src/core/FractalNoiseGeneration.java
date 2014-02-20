package core;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import core.base.SimpleCore;
import util.Interpolation;
import noise.FractalNoise;
import noise.InterpNoise;
import noise.NoiseArray;

/**
 * 
 * Demonstrates the generation of Fractal Noise.
 * 
 * @author F4113nb34st
 *
 */
public class FractalNoiseGeneration extends SimpleCore implements KeyListener
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
	
	private static final int WIDTH = 512; //try changing me!
	private static final int HEIGHT = 512; //try changing me!
	private static final NoiseArray noise = new NoiseArray(WIDTH, HEIGHT);
			
	private Interpolation inter = Interpolation.LINEAR;
	private long seed = (long)(Long.MAX_VALUE * Math.random());
	private int fineOctave = 0;
	private int broadOctave = 8;
	private double persistence = .5;
	private boolean useGradient = true;
	private boolean render = true;
	
	public FractalNoiseGeneration(String title)
	{
		super(title, WIDTH, HEIGHT);
	}

	@Override
	public void init()
	{
		clearFrame = false;
		frame.addKeyListener(this);//gotta listen for input
		regenNoise();
	}
	
	@Override 
	public void keyPressed(KeyEvent e)
	{
		boolean regen = false;
		switch(e.getKeyCode())
		{
			case KeyEvent.VK_SPACE://if space, go to next interpolation type
			{
				int index = inter.ordinal();
				index++;
				index %= Interpolation.values().length;
				inter = Interpolation.values()[index];
				regen = true;
				break;
			}
			case KeyEvent.VK_ENTER://if enter, save to desktop
			{
				takeScreenShot();
				break;
			}
			case KeyEvent.VK_Q:
			{
				if(fineOctave <= broadOctave)
				{
					fineOctave++;
					regen = true;
				}
				break;
			}
			case KeyEvent.VK_E:
			{
				if(fineOctave > 0)
				{
					fineOctave--;
					regen = true;
				}
				break;
			}
			case KeyEvent.VK_A:
			{
				if((1 << broadOctave) < Math.min(WIDTH, HEIGHT))
				{
					broadOctave++;
					regen = true;
				}
				break;
			}
			case KeyEvent.VK_D:
			{
				if(broadOctave > fineOctave)
				{
					broadOctave--;
					regen = true;
				}
				break;
			}
			case KeyEvent.VK_Z:
			{
				if(persistence < 2)
				{
					persistence += .1;
					regen = true;
				}
				break;
			}
			case KeyEvent.VK_C:
			{
				if(persistence > 0)
				{
					persistence -= .1;
					regen = true;
				}
				break;
			}
			case KeyEvent.VK_S:
			{
				seed = (long)(Long.MAX_VALUE * Math.random());
				regen = true;
				break;
			}
			case KeyEvent.VK_SHIFT:
			{
				useGradient = !useGradient;
				break;
			}
		}
		persistence = Math.round(persistence * 10) / 10D;
		if(regen)
		{
			regenNoise();
		}
	}
	
	private void regenNoise()
	{
		render = false;
		FractalNoise.fill_fractal_noise_array(noise, seed, InterpNoise.getAsFunction(inter), fineOctave, broadOctave, persistence);
		noise.normalize();
		render = true;
	}

	@Override
	public void update(BufferedImage image, Graphics2D g2)
	{
		if(render)
		{
			for(int i = 0; i < image.getWidth(); i++)
			{
				for(int j = 0; j < image.getHeight(); j++)
				{
					double value = noise.get(i, j);
					int red;
					int green;
					int blue;
					if(useGradient)
					{
						if(value <= .2)
						{
							red = 255;
							green = (int)Interpolation.COSINE.interpolate(0, 255, value / .2);
							blue = 0;
						}else
						if(value > .2 && value <= .4)
						{
							red = (int)Interpolation.COSINE.interpolate(255, 0, (value - .2) / .2);
							green = 255;
							blue = 0;
						}else
						if(value > .4 && value <= .6)
						{
							red = 0;
							green = 255;
							blue = (int)Interpolation.COSINE.interpolate(0, 255, (value - .4) / .2);
						}else
						if(value > .6 && value <= .8)
						{
							red = 0;
							green = (int)Interpolation.COSINE.interpolate(255, 0, (value - .6) / .2);
							blue = 255;
						}else
						{
							red = (int)Interpolation.COSINE.interpolate(0, 255, (value - .8) / .2);
							green = 0;
							blue = 255;
						}
					}else
					{
						red = (int)(value * 255);
						green = (int)(value * 255);
						blue = (int)(value * 255);
					}
					image.setRGB(i, j, (255 << 24) + (red << 16) + (green << 8) + blue);
				}
			}
		}
		
		g2.setColor(Color.BLACK);
		Font prevfont = g2.getFont();
		g2.setFont(prevfont.deriveFont(Font.BOLD, 15));
		g2.drawString(inter.getName(), WIDTH - 125, 18);
		g2.drawString("Fine Octave: " + fineOctave, WIDTH - 125, 32);
		g2.drawString("Broad Octave: " + broadOctave, WIDTH - 125, 46);
		g2.drawString("Persistence: " + persistence, WIDTH - 125, 60);
		g2.setFont(prevfont);
	}

	@Override public void keyReleased(KeyEvent arg0){}
	@Override public void keyTyped(KeyEvent arg0){}
}
