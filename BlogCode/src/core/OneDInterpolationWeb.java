package core;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import core.base.SimpleApplet;
import util.Interpolation;
import noise.NoiseGenerator;

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
	//the Unit size for the coord system
	private static final int UNIT = 50; //try changing me!
	//the number of random values
	private static final int VALUES = 9; //try changing me!
	//the width of the ticks on the sides
	private static final int TICK_WIDTH = 5;
	//the max height of the noise
	private static final int MAX_HEIGHT = 5;
	//the radius of the dots at the points
	private static final int DOT_RADIUS = 3;//you may need to lower me for large VALUES
	//the width of the screen
	private static final int WIDTH = (UNIT * (VALUES + 1)) + TICK_WIDTH + 1;
	//the height of the screen
	private static final int HEIGHT = (UNIT * MAX_HEIGHT) + TICK_WIDTH + 1;
	
	//the current interpolation function
	private Interpolation inter = Interpolation.LINEAR;
	//the Hermite tension
	private double tension = 0;
	//the Hermite bias
	private double bias = 0;
	//the noise generator instance
	private NoiseGenerator gen = new NoiseGenerator();
	
	//pass sizes
	public OneDInterpolationWeb()
	{
		super(WIDTH, HEIGHT);
	}

	@Override
	public void init()
	{
		//register key listener
		addKeyListener(this);
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
			case KeyEvent.VK_Q://if Q and in Hermite mode, increase tension
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
			case KeyEvent.VK_E://if E and in Hermite mode, decrease tension
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
			case KeyEvent.VK_A://if A and in Hermite mode, increase bias
			{
				if(inter == Interpolation.HERMITE)
				{
					bias += .1;
				}
				break;
			}
			case KeyEvent.VK_D://if D and in Hermite mode, decrease bias
			{
				if(inter == Interpolation.HERMITE)
				{
					bias -= .1;
				}
				break;
			}
			case KeyEvent.VK_S://if S and in Hermite mode, reset bias
			{
				if(inter == Interpolation.HERMITE)
				{
					bias = 0;
				}
				break;
			}
		}
		//round tension and bias
		tension = Math.round(tension * 10) / 10D;
		bias = Math.round(bias * 10) / 10D;
	}

	@Override
	public void update(BufferedImage image, Graphics2D g2)
	{
		//generate noise values
		double[] dots = new double[VALUES];
		for(int i = 0; i < dots.length; i++)
		{
			dots[i] = gen.noise_gen(0, i * UNIT);
		}
		
		//previous target y, used to ensure line is continuous
		int prevY = -Integer.MAX_VALUE;
		
		//for entire width
		for(int i = 0; i < image.getWidth(); i++)
		{
			//real x value
			int x = i - TICK_WIDTH;
			
			//find bot value
			int bottom = (x / UNIT) - 1;
			//find top value
			int top = bottom + 1;
			//find past value
			int past = bottom - 1;
			//find future value
			int future = top + 1;
			
			//the y value of the noise at this point
			int targetY = -Integer.MAX_VALUE;
			
			//if past out of range, just use bot
			if(past < 0)
			{
				past = bottom;
			}
			//if future out of range, just use top
			if(future >= VALUES)
			{
				future = top;
			}
			//if bot and top in range
			if(bottom >= 0 && top < VALUES)
			{
				//set target height
				targetY = (int)Math.round(UNIT * MAX_HEIGHT * inter.interpolate(dots[past], dots[bottom], dots[top], dots[future], (x % UNIT) / (double)UNIT, tension, bias));
			}
			
			//for entire height
			for(int j = 0; j < image.getHeight(); j++)
			{
				//get real y value
				int y = (image.getHeight() - j - 1) - TICK_WIDTH;
				
				//if a tick mark location
				if(x <= 0 && (y % UNIT) == 0)
				{
					image.setRGB(i, j, 0xFF808080);
				}else//if a tick mark location
				if(y <= 0 && (x % UNIT) == 0)
				{
					image.setRGB(i, j, 0xFF808080);
				}else//if axis lines
				if(y == 0 || x == 0)
				{
					image.setRGB(i, j, 0xFF808080);
				}else//if the y we want
				if(y == targetY)
				{
					image.setRGB(i, j, 0xFFFF0000);
				}else//ensure the discrete points connect
				if(prevY != -Integer.MAX_VALUE && targetY != -Integer.MAX_VALUE && ((y < targetY && y > prevY) || (y > targetY && y < prevY)))
				{
					image.setRGB(i, j, 0xFFFF0000);
				}
			}
			
			//set previous to current before we move on
			prevY = targetY;
		}
		
		//draw the dots
		//for all dot values
		for(int v = 0; v < VALUES; v++)
		{
			//get x coord
			int x = (v + 1) * UNIT;
			//get y coord
			int y = (int)(dots[v] * UNIT * MAX_HEIGHT);
			//for a box DOT_RADIUS around center
			for(int a = -DOT_RADIUS; a <= DOT_RADIUS; a++)
			{
				for(int b = -DOT_RADIUS; b <= DOT_RADIUS; b++)
				{
					//get draw x coord
					int i = x + a + TICK_WIDTH;
					//get draw y coord
					int j = (image.getHeight() - 1) - (y + b + TICK_WIDTH);
					//if within image
					if(i >= 0 && i < image.getWidth() && j >= 0 && j < image.getHeight())
					{
						//if distance to center is less than dotRadius
						if(((a * a) + (b * b)) <= (DOT_RADIUS * DOT_RADIUS))
						{
							//linearly interpolate dot
							double value = Math.sqrt((a * a) + (b * b)) / (DOT_RADIUS * DOT_RADIUS);
							int shade = (int)Interpolation.LINEAR.interpolate(64, 255, value);
							image.setRGB(i, j, (255 << 24) + (shade << 16) + (shade << 8) + shade);
						}
					}
				}
			}
		}
		
		//display infos
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
