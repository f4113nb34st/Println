package core;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import core.base.SimpleApplet;
import util.Interpolation;
import noise.InterpNoise;
import noise.NoiseArray;

/**
 * 
 * Demonstrates the generation of Interoplated Noise.
 * 
 * @author F4113nb34st
 *
 */
@SuppressWarnings("serial")
public class InterpNoiseGenerationWeb extends SimpleApplet implements KeyListener
{
	private static final int WIDTH = 350; //try changing me!
	private static final int HEIGHT = 350; //try changing me!
	private static final NoiseArray noise = new NoiseArray(WIDTH, HEIGHT);
			
	private Interpolation inter = Interpolation.LINEAR;
	private long seed = (long)(Long.MAX_VALUE * Math.random());
	private int periodX = 5;
	private int periodY = 5;
	private boolean useGradient = true;
	private boolean render = true;

	public InterpNoiseGenerationWeb()
	{
		super(WIDTH, HEIGHT);
	}

	@Override
	public void init()
	{
		clearFrame = false;
		addKeyListener(this);//gotta listen for input
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
				periodX++;
				if(periodX > WIDTH)
				{
					periodX = WIDTH;
				}
				regen = true;
				break;
			}
			case KeyEvent.VK_E:
			{
				periodX--;
				if(periodX < 1)
				{
					periodX = 1;
				}
				regen = true;
				break;
			}
			case KeyEvent.VK_A:
			{
				periodY++;
				if(periodY > HEIGHT)
				{
					periodY = HEIGHT;
				}
				regen = true;
				break;
			}
			case KeyEvent.VK_D:
			{
				periodY--;
				if(periodY < 1)
				{
					periodY = 1;
				}
				regen = true;
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
				regen = true;
				break;
			}
		}
		
		if(regen)
		{
			regenNoise();
		}
	}
	
	private void regenNoise()
	{
		render = false;
		InterpNoise.fill_interp_noise_array(noise, null, seed, inter, periodX, periodY);
		noise.normalize();
		setDirty();
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
		g2.drawString(inter.name().toLowerCase().replace('_', ' '), WIDTH - 70, 14);
		g2.drawString("X period: " + periodX, WIDTH - 90, 28);
		g2.drawString("Y period: " + periodY, WIDTH - 90, 42);
		g2.setFont(prevfont);
	}

	@Override public void keyReleased(KeyEvent arg0){}
	@Override public void keyTyped(KeyEvent arg0){}
}
