package core;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;
import core.base.NoiseDisplayCore;
import noise.AdvancedVoronoiNoise;
import noise.voronoi.*;

/**
 * 
 * Demonstrates the generation of Advanced Voronoi Noise.
 * 
 * @author F4113nb34st
 *
 */
public class AdvVoronoiNoiseGeneration extends NoiseDisplayCore
{
	public static void main(String[] args)
	{
		try
		{
			//just the general init stuff
			AdvVoronoiNoiseGeneration main = new AdvVoronoiNoiseGeneration("Voronoi Noise");
			main.init();
			main.renderLoop();
		}catch(Exception ex)//catch any exception
		{
			ex.printStackTrace();
		}
	}

	//the adv voronoi noise generator
	private AdvancedVoronoiNoise noiseFunc = new AdvancedVoronoiNoise(new ArrayList<VoronoiObject>(), DistanceFunction.Euclid, CombineFunction.F1);
	
	//pass title to SimpleCore
	public AdvVoronoiNoiseGeneration(String title)
	{
		super(title);
	}
	
	@Override 
	public boolean key(int code)
	{
		switch(code)
		{
			case KeyEvent.VK_SPACE://if space, next dis func
			{
				int index = noiseFunc.disFunc.ordinal();
				index++;
				index %= DistanceFunction.values().length;
				noiseFunc.disFunc = DistanceFunction.values()[index];
				return true;
			}
			case KeyEvent.VK_X://if x, next combine func
			{
				int index = noiseFunc.comFunc.ordinal();
				index++;
				index %= CombineFunction.values().length;
				noiseFunc.comFunc = CombineFunction.values()[index];
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void regenNoise(long seed)
	{
		Random rand = new Random(seed);
		noiseFunc.objects.clear();
		for(int i = 0; i < 20; i++)//generate our random objects
		{
			switch(rand.nextInt(3))
			{
				case 0://random point
					noiseFunc.objects.add(new VoronoiPoint(rand.nextInt(WIDTH), rand.nextInt(HEIGHT)));
					break;
				case 1://random circle
					noiseFunc.objects.add(new VoronoiCircle(rand.nextInt(WIDTH), rand.nextInt(HEIGHT), (int)(rand.nextDouble() * 50) + 10, rand.nextBoolean()));
					break;
				case 2://random line
					noiseFunc.objects.add(new VoronoiLine(rand.nextInt(WIDTH), rand.nextInt(HEIGHT), rand.nextInt(WIDTH), rand.nextInt(HEIGHT)));
					break;
			}
		}
		//fill the array
		noiseFunc.fillArray(noise);
	}

	@Override
	public void drawInfo(Graphics2D g2)
	{
		//display the infos
		drawString("Dis Func: " + noiseFunc.disFunc.name(), 175, g2);
		drawString("Com Func: " + noiseFunc.comFunc.name(), 175, g2);
	}
}
