package core.base;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import util.Interpolation;
import noise.NoiseArray;

/**
 * 
 * Core class that handles many aspects of displaying noise functions.
 * 
 * @author F4113nb34st
 *
 */
public abstract class NoiseDisplayCore extends SimpleCore implements KeyListener
{
	//the width of the screen
	public static final int WIDTH = 512; //try changing me!
	//the height of the screen
	public static final int HEIGHT = 512; //try changing me!
	//the noise array
	public static final NoiseArray noise = new NoiseArray(WIDTH, HEIGHT);
			
	/**
	 * The seed of the noise.
	 */
	private long seed = (long)(Long.MAX_VALUE * Math.random());
	/**
	 * Whether or not to use color.
	 */
	private boolean useGradient = true;
	/**
	 * Tells that we need a regen.
	 */
	private boolean regenDue = true;
	
	//passes title and sizes to SimpleCore
	public NoiseDisplayCore(String title)
	{
		super(title, WIDTH, HEIGHT);
	}
	
	/**
	 * Handles key presses. Return true to regenerate noise afterwards.
	 * @param code The key code (see KeyEvent)
	 * @return True to regen noise.
	 */
	public abstract boolean key(int code);

	@Override
	public void init()
	{
		//don't clear frame between renders
		clearFrame = false;
		//add key listener
		frame.addKeyListener(this);
		//init noise array
		regenNoise();
	}
	
	@Override 
	public void keyPressed(KeyEvent e)
	{
		boolean regen = false;
		switch(e.getKeyCode())
		{
			case KeyEvent.VK_ENTER://if enter, save to desktop
			{
				takeScreenShot();
				break;
			}
			case KeyEvent.VK_S://if S, new seed
			{
				seed = (long)(Long.MAX_VALUE * Math.random());
				regen = true;
				break;
			}
			case KeyEvent.VK_SHIFT://if shift, toggle gradient mode
			{
				useGradient = !useGradient;
				break;
			}
			default://otherwise pass to client
			{
				regen = key(e.getKeyCode());
				break;
			}
		}
		//if we should regen, do so
		if(regen)
		{
			regenDue = true;
		}
	}
	
	private void regenNoise()
	{
		//regens the noise
		regenNoise(seed);
		//normalizes noise
		noise.normalize();
	}
	
	/**
	 * Should regenerate the noise array with the given seed.
	 * @param seed The seed to use.
	 */
	public abstract void regenNoise(long seed);

	@Override
	public void update(BufferedImage image, Graphics2D g2)
	{
		//if regen needed, do so
		if(regenDue)
		{
			regenNoise();
			regenDue = false;
		}
		
		//for all pixels
		for(int i = 0; i < image.getWidth(); i++)
		{
			for(int j = 0; j < image.getHeight(); j++)
			{
				//get value
				double value = noise.get(i, j);
				int red;
				int green;
				int blue;
				//if gradient, convert to color
				if(useGradient)
				{
					if(value <= .2)
					{
						red = 255;
						green = (int)Interpolation.LINEAR.interpolate(0, 255, value / .2);
						blue = 0;
					}else
					if(value > .2 && value <= .4)
					{
						red = (int)Interpolation.LINEAR.interpolate(255, 0, (value - .2) / .2);
						green = 255;
						blue = 0;
					}else
					if(value > .4 && value <= .6)
					{
						red = 0;
						green = 255;
						blue = (int)Interpolation.LINEAR.interpolate(0, 255, (value - .4) / .2);
					}else
					if(value > .6 && value <= .8)
					{
						red = 0;
						green = (int)Interpolation.LINEAR.interpolate(255, 0, (value - .6) / .2);
						blue = 255;
					}else
					{
						red = (int)Interpolation.LINEAR.interpolate(0, 255, (value - .8) / .2);
						green = 0;
						blue = 255;
					}
				}else//else use greyscale
				{
					red = (int)(value * 255);
					green = (int)(value * 255);
					blue = (int)(value * 255);
				}
				//set pixel
				image.setRGB(i, j, (255 << 24) + (red << 16) + (green << 8) + blue);
			}
		}
		
		//change to bold font
		g2.setColor(Color.BLACK);
		Font prevfont = g2.getFont();
		g2.setFont(prevfont.deriveFont(Font.BOLD, 15));
		//have user draw info
		drawInfo(g2);
		//reset the line
		line = 1;
		//reset the font
		g2.setFont(prevfont);
	}
	
	/**
	 * Draws the info in the top right corner. Use in conjunction with drawString(String, int, Graphics2D)
	 */
	public abstract void drawInfo(Graphics2D g2);
	
	private int line = 1;
	/**
	 * Draws the given string in the top left corner.
	 * @param string The string to draw.
	 * @param size The width of the string.
	 * @param g2 The graphics instance.
	 */
	public void drawString(String string, int size, Graphics2D g2)
	{
		g2.drawString(string, WIDTH - size, 18 * line + 2);
		line++;
	}

	@Override public void keyReleased(KeyEvent arg0){}
	@Override public void keyTyped(KeyEvent arg0){}
}
