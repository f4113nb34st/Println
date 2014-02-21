package core.base;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import util.FrameRate;
import util.Interpolation;

public abstract class SimpleCore
{
	/**
	 * True if the game is running.
	 */
	private boolean running;
	/**
	 * The JFrame of the program.
	 */
	public SimpleFrame frame;
	
	/**
	 * True if the frame should be clear between renders.
	 */
	public boolean clearFrame = true;
	
	//stuff for saving screenshots
	private boolean screenshot = false;
	private boolean saveSuccessful = true;
	private static final int FADE_TIME = 1000;
	private boolean fading = false;
	private long fadeStart;
	
	private String title;
	
	public SimpleCore(String title, int width, int height)
	{
		this.title = title;
		frame = new SimpleFrame();
		
		Rectangle r = frame.getGraphicsConfiguration().getBounds();
		
		int screenWidth = r.width;
		int screenHeight = r.height;
		r.x = (screenWidth - width - 50) / 2;
		r.y = (screenHeight - height - 50) / 2;
		r.width = width + 50;
		r.height = height + 50;
		
		frame.setBounds(r);
		
		frame.setResizable(true);
		frame.setFocusTraversalKeysEnabled(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		screen = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		running = true;
	}
	
	public abstract void init();
	
	public abstract void update(BufferedImage image, Graphics2D g2);
	
	public void takeScreenShot()
	{
		screenshot = true;
		fading = true;
		fadeStart = System.currentTimeMillis();
	}
	
	/**
	 * The main render loop.
	 */
	public void renderLoop()
	{	
		while(running)
		{	
			paint();
		}
	}
	
	/**
	 * The image on the screen.
	 */
	protected BufferedImage screen;
	/**
	 * The buffer image to draw to.
	 */
	protected BufferedImage buffer;
	
	/**
	 * Paints the screen onto the canvas.
	 */
	protected void paint()
	{
		Graphics2D g2 = buffer.createGraphics();
		if(clearFrame)
		{
			g2.setColor(Color.WHITE);
			g2.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());
		}
		
		update(buffer, g2);
		handleScreenShot(buffer, g2);
		
		g2.dispose();
		
		swapBuffers();
		
		frame.repaint();
		FrameRate.poll();
		
		frame.setTitle(title + ": " + (int)FrameRate.getFrameRate());
	}
	
	private void handleScreenShot(BufferedImage image, Graphics2D g2)
	{
		if(screenshot)
		{
			screenshot = false;
			
			final BufferedImage save = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D temp = save.createGraphics();
			temp.drawImage(image, 0, 0, null);
			temp.dispose();
			
			new Thread(new Runnable()//save in a secondary thread so we keep rendering
			{
				@Override
				public void run()
				{
					try
					{
						ImageIO.write(save, "png", new File("screenshot.png"));
						saveSuccessful = true;
					} catch(IOException ex)
					{
						System.err.println("[TwoDInterpolation][Warning] Error writing screenshot!");
						ex.printStackTrace();
						saveSuccessful = false;
						fadeStart = System.currentTimeMillis();
					}
				}
			}).start();
		}
		
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
				if(saveSuccessful)
				{
					g2.drawRect((image.getWidth() / 2) - 78 - border + i, (image.getHeight() / 2) - 20 - border + i, 159 + (border * 2) - (i * 2), 20 + (border * 2) - 1 - (i * 2));
				}else
				{
					g2.drawRect((image.getWidth() / 2) - 64 - border + i, (image.getHeight() / 2) - 20 - border + i, 128 + (border * 2) - (i * 2), 20 + (border * 2) - 1 - (i * 2));
				}
			}
			g2.setColor(new Color(1F, 1F, 1F));
			if(saveSuccessful)
			{
				g2.fillRect((image.getWidth() / 2) - 78, (image.getHeight() / 2) - 20, 160, 20);
			}else
			{
				g2.fillRect((image.getWidth() / 2) - 64, (image.getHeight() / 2) - 20, 129, 20);
			}
			
			Font font = g2.getFont();
			g2.setFont(font.deriveFont(Font.BOLD, 20));
			if(saveSuccessful)
			{
				g2.setColor(new Color(shade, 1, shade));
				g2.drawString("Save Successful.", (image.getWidth() / 2) - 78, image.getHeight() / 2);
			}else
			{
				g2.setColor(new Color(1, shade, shade));
				g2.drawString("Error Saving!", (image.getWidth() / 2) - 63, image.getHeight() / 2);
			}
			g2.setFont(font);
		}
	}
	
	private synchronized void swapBuffers()
	{
		BufferedImage temp = screen;
		screen = buffer;
		buffer = temp;
	}
	
	private synchronized void paintFrame(Graphics g)
	{
		if(screen != null)
		{
			g.drawImage(screen, (frame.getWidth() - screen.getWidth()) / 2, (frame.getHeight() - screen.getHeight()) / 2, null);
		}
	}
	
	@SuppressWarnings("serial")
	public class SimpleFrame extends JFrame
	{
		@Override
		public void paint(Graphics g)
		{
			paintFrame(g);
		}
	}
}
