package core;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;
import core.base.NoiseDisplayCoreWeb;
import noise.AdvancedVoronoiNoise;
import noise.voronoi.*;

/**
 * 
 * Demonstrates the generation of Advanced Voronoi Noise.
 * 
 * @author F4113nb34st
 *
 */
@SuppressWarnings("serial")
public class AdvVoronoiNoiseGenerationWeb extends NoiseDisplayCoreWeb
{
	//list of randomly generated objects to render
	private ArrayList<VoronoiObject> objects = new ArrayList<VoronoiObject>();
	//our distance function
	private DistanceFunction disFunc = DistanceFunction.Euclid;
	//our combine function
	private CombineFunction comFunc = CombineFunction.F1;
	
	@Override 
	public boolean key(int code)
	{
		switch(code)
		{
			case KeyEvent.VK_SPACE://if space, next dis func
			{
				int index = disFunc.ordinal();
				index++;
				index %= DistanceFunction.values().length;
				disFunc = DistanceFunction.values()[index];
				return true;
			}
			case KeyEvent.VK_X://if x, next combine func
			{
				int index = comFunc.ordinal();
				index++;
				index %= CombineFunction.values().length;
				comFunc = CombineFunction.values()[index];
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void regenNoise(long seed)
	{
		Random rand = new Random(seed);
		objects.clear();
		for(int i = 0; i < 20; i++)//generate our random objects
		{
			switch(rand.nextInt(3))
			{
				case 0://random point
					objects.add(new VoronoiPoint(rand.nextInt(WIDTH), rand.nextInt(HEIGHT)));
					break;
				case 1://random circle
					objects.add(new VoronoiCircle(rand.nextInt(WIDTH), rand.nextInt(HEIGHT), (int)(rand.nextDouble() * 50) + 10, rand.nextBoolean()));
					break;
				case 2://random line
					objects.add(new VoronoiLine(rand.nextInt(WIDTH), rand.nextInt(HEIGHT), rand.nextInt(WIDTH), rand.nextInt(HEIGHT)));
					break;
			}
		}
		//fill the array
		AdvancedVoronoiNoise.fill_adv_voronoi_noise_array(noise, objects, disFunc, comFunc);
	}

	@Override
	public void drawInfo(Graphics2D g2)
	{
		//display the infos
		drawString("Dis Func: " + disFunc.name(), 175, g2);
		drawString("Com Func: " + comFunc.name(), 175, g2);
	}
}
