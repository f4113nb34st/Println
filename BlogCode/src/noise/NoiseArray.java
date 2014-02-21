package noise;

import util.Util;

public class NoiseArray
{
	public double[][] noise;
	public int minX;
	public int minY;
	public int maxX;
	public int maxY;
	public boolean wrapX = true;
	public boolean wrapY = true;
	
	public double amplitude = 1;
	public double offset = 0;
	
	public NoiseArray(int w, int h)
	{
		this(new double[w][h], w, h);
	}
	
	public NoiseArray(double[][] n, int w, int h)
	{
		noise = n;
		setBounds(w, h);
	}
	
	public NoiseArray(double[][] n, int miX, int miY, int maX, int maY)
	{
		noise = n;
		setBounds(miX, miY, maX, maY);
	}
	
	public void setBounds(int miX, int miY, int maX, int maY)
	{
		minX = miX;
		minY = miY;
		maxX = maX;
		maxY = maY;
	}
	
	public void setBounds(int w, int h)
	{
		minX = 0;
		minY = 0;
		maxX = w - 1;
		maxY = h - 1;
		if((maxX - minX) >= noise.length || (maxY - minY) >= noise[0].length)
		{
			noise = new double[maxX - minX + 1][maxY - minY + 1];
		}
	}
	
	public int getWidth()
	{
		return maxX + 1 - minX;
	}
	
	public int getHeight()
	{
		return maxY + 1 - minY;
	}
	
	public void set(int x, int y, double value)
	{
		x = fix(x, minX, maxX, wrapX);
		y = fix(y, minY, maxY, wrapY);
		noise[x][y] = (value * amplitude) + offset;
	}
	
	public double get(int x, int y)
	{
		x = fix(x, minX, maxX, wrapX);
		y = fix(y, minY, maxY, wrapY);
		return noise[x][y];
	}
	
	/**
	 * Normalizes the values of this noise array.
	 * (resizes so the max = 1 and min = 0)
	 */
	public void normalize()
	{
		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;
		
		for(int x = minX; x <= maxX; x++)
		{
			for(int y = minY; y <= maxY; y++)
			{
				min = Math.min(min, noise[x][y]);
				max = Math.max(max, noise[x][y]);
			}
		}
		
		double multi = 1 / (max - min);
		
		for(int x = minX; x <= maxX; x++)
		{
			for(int y = minY; y <= maxY; y++)
			{
				noise[x][y] = ((noise[x][y] - min) * multi);
			}
		}
	}
	
	private static final int fix(int val, int min, int max, boolean wrap)
	{
		if(wrap)
		{
			return Util.wrap(val, min, max);
		}else
		{
			return Util.clip(val, min, max);
		}
	}
}
