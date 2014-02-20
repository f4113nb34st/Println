package core.base;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JApplet;
import util.Interpolation;

@SuppressWarnings("serial")
public abstract class SimpleApplet extends JApplet
{
	/**
	 * True if the frame should be clear between renders.
	 */
	public boolean clearFrame = true;
	
	private boolean dirty = true;
	
	//stuff for saving screenshots
	private static final int FADE_TIME = 1000;
	private boolean fading = false;
	private long fadeStart;
	
	public SimpleApplet(int width, int height)
	{
		setBounds(0, 0, width + (BORDER_WIDTH * 2), height + (BORDER_WIDTH * 2));
		buffer1 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		buffer2 = new BufferedImage(width + (BORDER_WIDTH * 2), height + (BORDER_WIDTH * 2), BufferedImage.TYPE_INT_ARGB);
		
		Graphics g2 = buffer2.createGraphics();
		for(int i = 0; i <= BORDER_WIDTH; i++)
		{
			float value = (float)Interpolation.COSINE.interpolate(0, 1, i / (double)BORDER_WIDTH);
			g2.setColor(new Color(value, value, value));
			g2.drawRect(i, i, getWidth() - 1 - (2 * i), getHeight() - 1 - (2 * i));
		}
		g2.dispose();
		
		
		setFocusable(true);
		
		Thread thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				while(true)
				{
					if(dirty)
					{
						dirty = false;
						repaint();
					}
					
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
		thread.setDaemon(true);
		thread.start();
	}
	
	public void takeScreenShot()
	{
		fading = true;
		fadeStart = System.currentTimeMillis();
		setDirty();
	}
	
	/**
	 * Tells applet to update screen.
	 */
	public void setDirty()
	{
		dirty = true;
	}
	
	private static final int BORDER_WIDTH = 10;
	
	private BufferedImage buffer1;
	private BufferedImage buffer2;
	
	public abstract void update(BufferedImage image, Graphics2D g2);
	
	@Override
	public void paint(Graphics g)
	{
		Graphics2D g2 = buffer1.createGraphics();
		if(clearFrame)
		{
			g2.setColor(Color.WHITE);
			g2.fillRect(0, 0, buffer1.getWidth(), buffer1.getHeight());
		}
		
		update(buffer1, g2);
		handleScreenShot(buffer1, g2);

		g2.dispose();
		
		g2 = buffer2.createGraphics();
		g2.drawImage(buffer1, BORDER_WIDTH, BORDER_WIDTH, null);
		g2.dispose();
		
		g.drawImage(buffer2, 0, 0, null);
	}
	
	private void handleScreenShot(BufferedImage image, Graphics2D g2)
	{
		if(fading)
		{
			long dif = System.currentTimeMillis() - fadeStart;
			if(dif > FADE_TIME)
			{
				fading = false;
			}
			double mu = dif / (double)FADE_TIME;
			float shade = (float)Interpolation.COSINE.interpolate(0, 1, mu);
			
			int border = 10;
			
			for(int i = 0; i < border; i++)
			{
				float shade2 = (float)Interpolation.COSINE.interpolate(0, 1, Math.min(i / (double)border, 1));
				g2.setColor(new Color(shade2, shade2, shade2));
				g2.drawRect((image.getWidth() / 2) - 125 - border + i, (image.getHeight() / 2) - 10 - border + i, 250 + (border * 2) - (i * 2), 10 + (border * 2) - 1 - (i * 2));
			}
			g2.setColor(new Color(1F, 1F, 1F));
			g2.fillRect((image.getWidth() / 2) - 125, (image.getHeight() / 2) - 10, 251, 10);
			
			Font font = g2.getFont();
			g2.setFont(font.deriveFont(Font.BOLD));
			
			g2.setColor(new Color(1, shade, shade));
			g2.drawString("Cannot take screenshots in the web version.", (image.getWidth() / 2) - 125, image.getHeight() / 2);
				
			g2.setFont(font);
			
			setDirty();
		}
	}
}
