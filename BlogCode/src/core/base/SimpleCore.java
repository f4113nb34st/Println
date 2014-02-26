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

/**
 * 
 * Simple core class for gui programs that handles most of the general init stuff.
 * 
 * @author F4113nb34st
 *
 */
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
	
	/**
	 * The title of the program.
	 */
	private String title;
	
	/**
	 * Creates a new Simple Core with the given title and size.
	 * @param title The title of the JFrame.
	 * @param width The initial width of the JFrame.
	 * @param height The initial height of the JFrame.
	 */
	public SimpleCore(String title, int width, int height)
	{
		//set title
		this.title = title;
		//create frame
		frame = new SimpleFrame();
		
		//set bounds
		Rectangle r = frame.getGraphicsConfiguration().getBounds();
		int screenWidth = r.width;
		int screenHeight = r.height;
		r.x = (screenWidth - width - 50) / 2;
		r.y = (screenHeight - height - 50) / 2;
		r.width = width + 50;
		r.height = height + 50;
		frame.setBounds(r);
		
		//init frame
		frame.setResizable(true);
		frame.setFocusTraversalKeysEnabled(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		//create buffers
		screen = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		//set running to true
		running = true;
	}
	
	/**
	 * Perform init work here.
	 */
	public abstract void init();
	
	/**
	 * Renders the program to the given image.
	 * @param image The image to render to.
	 * @param g2 The graphics of the image.
	 */
	public abstract void update(BufferedImage image, Graphics2D g2);
	
	/**
	 * Takes a screenshot.
	 */
	public void takeScreenShot()
	{
		//take screenshot
		screenshot = true;
		//start fading
		fading = true;
		//set fade start time
		fadeStart = System.currentTimeMillis();
	}
	
	/**
	 * The main render loop.
	 */
	public void renderLoop()
	{	
		while(running)//til done running, keep painting
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
		//create buffer graphics
		Graphics2D g2 = buffer.createGraphics();
		//if clear frame between renders
		if(clearFrame)
		{
			//fill with white
			g2.setColor(Color.WHITE);
			g2.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());
		}
		//let client render
		update(buffer, g2);
		//handle screen shot stuff
		handleScreenShot(buffer, g2);
		//get rid of graphics
		g2.dispose();
		
		//swap the buffers
		swapBuffers();
		
		//repaint the frame
		frame.repaint();
		//poll the frame rate ticker
		FrameRate.poll();
		//update framerate in title
		frame.setTitle(title + ": " + (int)FrameRate.getFrameRate());
	}
	
	/**
	 * Handles taking a screen shot and fading the message.
	 * @param image The image to render to.
	 * @param g2 The graphics of the image.
	 */
	private void handleScreenShot(BufferedImage image, Graphics2D g2)
	{
		//if we need to take a screenshot
		if(screenshot)
		{
			//don't repeat take
			screenshot = false;
			
			//create copy of screen
			final BufferedImage save = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D temp = save.createGraphics();
			temp.drawImage(image, 0, 0, null);
			temp.dispose();
			
			//save in a secondary thread so we keep rendering
			new Thread(new Runnable()
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
		
		//if still fading the message
		if(fading)
		{
			//get time passed
			long dif = System.currentTimeMillis() - fadeStart;
			//if we are done
			if(dif > FADE_TIME)
			{
				//stop fading
				fading = false;
			}
			//get frac time
			double mu = dif / (double)FADE_TIME;
			//get shade of the text
			float shade = (float)Interpolation.COSINE.interpolate(0, 1, mu);
			
			//border size
			int border = 10;
			
			//draw gradient border
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
			//fill center
			g2.setColor(new Color(1F, 1F, 1F));
			if(saveSuccessful)
			{
				g2.fillRect((image.getWidth() / 2) - 78, (image.getHeight() / 2) - 20, 160, 20);
			}else
			{
				g2.fillRect((image.getWidth() / 2) - 64, (image.getHeight() / 2) - 20, 129, 20);
			}
			
			//set font to bold
			Font font = g2.getFont();
			g2.setFont(font.deriveFont(Font.BOLD, 20));
			//draw save successful specific text
			if(saveSuccessful)
			{
				g2.setColor(new Color(shade, 1, shade));
				g2.drawString("Save Successful.", (image.getWidth() / 2) - 78, image.getHeight() / 2);
			}else
			{
				g2.setColor(new Color(1, shade, shade));
				g2.drawString("Error Saving!", (image.getWidth() / 2) - 63, image.getHeight() / 2);
			}
			//reset font
			g2.setFont(font);
		}
	}
	
	/**
	 * Just swaps the screen with the back buffer.
	 */
	private synchronized void swapBuffers()
	{
		BufferedImage temp = screen;
		screen = buffer;
		buffer = temp;
	}
	
	/**
	 * Paints the frame.
	 * @param g The graphics to draw to.
	 */
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
