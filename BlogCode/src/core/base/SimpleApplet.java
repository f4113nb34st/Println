package core.base;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JApplet;
import util.Interpolation;

/**
 * 
 * Simple core class for applets that handles most of the general init stuff.
 * 
 * @author F4113nb34st
 *
 */
@SuppressWarnings("serial")
public abstract class SimpleApplet extends JApplet
{
	/**
	 * True if the frame should be clear between renders.
	 */
	public boolean clearFrame = true;
	/**
	 * True if thing shave changed and we need to re-render
	 */
	private boolean dirty = true;
	
	//stuff for saving screenshots
	private static final int FADE_TIME = 1000;
	private boolean fading = false;
	private long fadeStart;
	
	public SimpleApplet(int width, int height)
	{
		//sets the bounds
		setBounds(0, 0, width + (BORDER_WIDTH * 2), height + (BORDER_WIDTH * 2));
		//generates the buffers
		buffer1 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		buffer2 = new BufferedImage(width + (BORDER_WIDTH * 2), height + (BORDER_WIDTH * 2), BufferedImage.TYPE_INT_ARGB);
		
		//draws border
		Graphics g2 = buffer2.createGraphics();
		for(int i = 0; i <= BORDER_WIDTH; i++)
		{
			float value = (float)Interpolation.COSINE.interpolate(0, 1, i / (double)BORDER_WIDTH);
			g2.setColor(new Color(value, value, value));
			g2.drawRect(i, i, getWidth() - 1 - (2 * i), getHeight() - 1 - (2 * i));
		}
		g2.dispose();
		
		//let us get focus
		setFocusable(true);
		
		//start the render thread
		Thread thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				//run infinitly
				while(true)
				{
					//if dirty, repaint
					if(dirty)
					{
						dirty = false;
						repaint();
					}
					
					//otherwise sleep
					try
					{
						Thread.sleep(100);
					} catch(InterruptedException ex)
					{
						ex.printStackTrace();
					}
				}
			}
		});
		//if other thread dies, it's ok to kill this one
		thread.setDaemon(true);
		//start it
		thread.start();
	}
	
	/**
	 * Takes a screen shot of the screen.
	 */
	public void takeScreenShot()
	{
		//start fading the message
		fading = true;
		//set start time
		fadeStart = System.currentTimeMillis();
		//dirty screen
		setDirty();
	}
	
	/**
	 * Tells applet to update screen.
	 */
	public void setDirty()
	{
		dirty = true;
	}
	
	/**
	 * The border width.
	 */
	private static final int BORDER_WIDTH = 10;
	/**
	 * The first buffer.
	 */
	private BufferedImage buffer1;
	/**
	 * The second buffer.
	 */
	private BufferedImage buffer2;
	
	/**
	 * Draws the screen on the given image and its graphics.
	 * @param image The image to draw on.
	 * @param g2 The graphics of the image.
	 */
	public abstract void update(BufferedImage image, Graphics2D g2);
	
	@Override
	public void paint(Graphics g)
	{
		//create graphics
		Graphics2D g2 = buffer1.createGraphics();
		//if clear between frames
		if(clearFrame)
		{
			//fill with white
			g2.setColor(Color.WHITE);
			g2.fillRect(0, 0, buffer1.getWidth(), buffer1.getHeight());
		}
		//let client draw
		update(buffer1, g2);
		//handle screen shot stuff
		handleScreenShot(buffer1, g2);
		//get rid of graphics
		g2.dispose();
		
		//draw to buffer2
		g2 = buffer2.createGraphics();
		g2.drawImage(buffer1, BORDER_WIDTH, BORDER_WIDTH, null);
		g2.dispose();
		
		//draw buffer2 on g
		g.drawImage(buffer2, 0, 0, null);
	}
	
	/**
	 * Handles the taking of screens shots and the rendering of the message.
	 * @param image The image to draw on.
	 * @param g2 The graphics of the image.
	 */
	private void handleScreenShot(BufferedImage image, Graphics2D g2)
	{
		//if currently fading the message
		if(fading)
		{
			//get time since start
			long dif = System.currentTimeMillis() - fadeStart;
			//if we are done
			if(dif > FADE_TIME)
			{
				//stop fading
				fading = false;
			}
			//set frac time
			double mu = dif / (double)FADE_TIME;
			//get the shade of the message
			float shade = (float)Interpolation.COSINE.interpolate(0, 1, mu);
			
			//the border size of the message
			int border = 10;
			
			//draw gradient border
			for(int i = 0; i < border; i++)
			{
				float shade2 = (float)Interpolation.COSINE.interpolate(0, 1, Math.min(i / (double)border, 1));
				g2.setColor(new Color(shade2, shade2, shade2));
				g2.drawRect((image.getWidth() / 2) - 125 - border + i, (image.getHeight() / 2) - 10 - border + i, 250 + (border * 2) - (i * 2), 10 + (border * 2) - 1 - (i * 2));
			}
			//fill center area
			g2.setColor(new Color(1F, 1F, 1F));
			g2.fillRect((image.getWidth() / 2) - 125, (image.getHeight() / 2) - 10, 251, 10);
			
			//set bold font
			Font font = g2.getFont();
			g2.setFont(font.deriveFont(Font.BOLD));
			
			//draw the string in red
			g2.setColor(new Color(1, shade, shade));
			g2.drawString("Cannot take screenshots in the web version.", (image.getWidth() / 2) - 125, image.getHeight() / 2);
				
			//reset font
			g2.setFont(font);
			
			//keep updating screen
			setDirty();
		}
	}
}
