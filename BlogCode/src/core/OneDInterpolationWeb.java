package core;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import core.base.SimpleApplet;
import util.Interpolation;
import noise.BasicNoise;

/**
 * 
 * Demonstrates different types of 1D interpolation.
 * 
 * @author F4113nb34st
 *
 */
@SuppressWarnings("serial")
public class OneDInterpolationWeb extends SimpleApplet implements KeyListener
{
	private static final int UNIT = 50; //try changing me!
	private static final int VALUES = 9; //try changing me!
	private static final int TICK_WIDTH = 5;
	private static final int MAX_HEIGHT = 5;
	private static final int DOT_RADIUS = 3;//you may need to lower me for large VALUES
	private static final int WIDTH = (UNIT * (VALUES + 1)) + TICK_WIDTH + 1;
	private static final int HEIGHT = (UNIT * MAX_HEIGHT) + TICK_WIDTH + 1;
	
	private Interpolation inter = Interpolation.LINEAR;
	private double tension = 0;
	private double bias = 0;
	
	public OneDInterpolationWeb()
	{
		super(WIDTH, HEIGHT);
	}

	@Override
	public void init()
	{
		addKeyListener(this);//gotta listen for input
	}
	
	@Override 
	public void keyPressed(KeyEvent e)
	{
		setDirty();
		switch(e.getKeyCode())
		{
			case KeyEvent.VK_SPACE://if space, go to next interpolation type
			{
				int index = inter.ordinal();
				index++;
				index %= Interpolation.values().length;
				inter = Interpolation.values()[index];
				break;
			}
			case KeyEvent.VK_ENTER://if enter, save to desktop
			{
				takeScreenShot();
				break;
			}
			case KeyEvent.VK_Q:
			{
				if(inter == Interpolation.HERMITE)
				{
					tension += .1;
					if(tension > 1)
					{
						tension = 1;
					}
				}
				break;
			}
			case KeyEvent.VK_E:
			{
				if(inter == Interpolation.HERMITE)
				{
					tension -= .1;
					if(tension < -1)
					{
						tension = -1;
					}
				}
				break;
			}
			case KeyEvent.VK_A:
			{
				if(inter == Interpolation.HERMITE)
				{
					bias += .1;
				}
				break;
			}
			case KeyEvent.VK_D:
			{
				if(inter == Interpolation.HERMITE)
				{
					bias -= .1;
				}
				break;
			}
			case KeyEvent.VK_S:
			{
				if(inter == Interpolation.HERMITE)
				{
					bias = 0;
				}
				break;
			}
		}
		tension = Math.round(tension * 10) / 10D;
		bias = Math.round(bias * 10) / 10D;
	}

	@Override
	public void update(BufferedImage image, Graphics2D g2)
	{
		double[] dots = new double[VALUES];
		for(int i = 0; i < dots.length; i++)
		{
			dots[i] = BasicNoise.noise_gen(i * UNIT, 0);
		}
		
		int prevY = -Integer.MAX_VALUE;
		
		for(int i = 0; i < image.getWidth(); i++)
		{
			int x = i - TICK_WIDTH;
			
			int bottom = (x / UNIT) - 1;
			int top = bottom + 1;
			int past = bottom - 1;
			int future = top + 1;
			int targetY = -Integer.MAX_VALUE;
			if(past < 0)
			{
				past = bottom;
			}
			if(future >= VALUES)
			{
				future = top;
			}
			if(bottom >= 0 && top < VALUES)
			{
				targetY = (int)Math.round(UNIT * MAX_HEIGHT * inter.interpolate(dots[past], dots[bottom], dots[top], dots[future], (x % UNIT) / (double)UNIT, tension, bias));
			}
			
			for(int j = 0; j < image.getHeight(); j++)
			{
				int y = (image.getHeight() - j - 1) - TICK_WIDTH;
				
				if(x <= 0 && (y % UNIT) == 0)
				{
					image.setRGB(i, j, 0xFF808080);
				}else
				if(y <= 0 && (x % UNIT) == 0)
				{
					image.setRGB(i, j, 0xFF808080);
				}else
				if(y == 0 || x == 0)
				{
					image.setRGB(i, j, 0xFF808080);
				}else
				if(y == targetY)
				{
					image.setRGB(i, j, 0xFFFF0000);
				}else
				if(prevY != -Integer.MAX_VALUE && targetY != -Integer.MAX_VALUE && ((y < targetY && y > prevY) || (y > targetY && y < prevY)))
				{
					image.setRGB(i, j, 0xFFFF0000);
				}
			}
			
			prevY = targetY;
		}
		
		for(int v = 0; v < VALUES; v++)
		{
			int x = (v + 1) * UNIT;
			
			int y = (int)(dots[v] * UNIT * MAX_HEIGHT);
			for(int a = -DOT_RADIUS; a <= DOT_RADIUS; a++)
			{
				for(int b = -DOT_RADIUS; b <= DOT_RADIUS; b++)
				{
					int i = x + a + TICK_WIDTH;
					int j = (image.getHeight() - 1) - (y + b + TICK_WIDTH);
					if(i >= 0 && i < image.getWidth() && j >= 0 && j < image.getHeight())
					{
						if(((a * a) + (b * b)) <= (DOT_RADIUS * DOT_RADIUS))
						{
							double value = Math.sqrt((a * a) + (b * b)) / (DOT_RADIUS * DOT_RADIUS);
							int shade = (int)Interpolation.LINEAR.interpolate(64, 255, value);
							image.setRGB(i, j, (255 << 24) + (shade << 16) + (shade << 8) + shade);
						}
					}
				}
			}
		}
		
		g2.setColor(Color.BLACK);
		g2.drawString(inter.getName(), WIDTH - 72, 14);
		if(inter == Interpolation.HERMITE)
		{
			g2.drawString("Tension: " + tension, WIDTH - 72, 28);
			g2.drawString("Bias: " + bias, WIDTH - 72, 42);
		}
	}

	@Override public void keyReleased(KeyEvent arg0){}
	@Override public void keyTyped(KeyEvent arg0){}
}
