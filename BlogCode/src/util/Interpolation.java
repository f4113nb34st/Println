package util;

/**
 * 
 * Stores different methods of interpolation.
 * 
 * @author F4113nb34st
 *
 */
public enum Interpolation
{
	/**
	 * Simple interpolation.
	 * Not continuous, nor is derivative.
	 */
	LINEAR, 
	/**
	 * Smoothed interpolation.
	 * Continuous, but derivative isn't.
	 */
	COSINE,
	/**
	 * Smoothed interpolation.
	 * Requires extra samples.
	 * Continuous, and derivative too.
	 */
	CUBIC, 
	/**
	 * Smoothed interpolation.
	 * Requires extra samples.
	 * Continuous, and derivative too.
	 * Less lax than cubic.
	 */
	CATMULL_ROM, 
	/**
	 * Smoothed interpolation.
	 * Requires extra samples.
	 * Continuous, and derivative too.
	 * Allows extra parameters.
	 */
	HERMITE;
	
	/**
	 * Returns true if this interpolation function needs extra values.
	 * @return True if extended.
	 */
	public boolean extended()
	{
		return this == CUBIC || this == CATMULL_ROM || this == HERMITE;
	}
	
	/**
	 * Returns the simple every-day name of this function.
	 * @return The name.
	 */
	public String getName()
	{
		switch(this)
		{
			case LINEAR:
				return "Linear";
			case COSINE:
				return "Cosine";
			case CUBIC:
				return "Cubic";
			case CATMULL_ROM:
				return "Catmull Rom";
			case HERMITE:
				return "Hermite";
		}
		return "";
	}
	
	/**
	 * Interpolates between the given values using this type interpolation. (top = 1, bottom = 0)
	 * @param bottom The value at 0.
	 * @param top The value at 1.
	 * @param mu The distance between.
	 * @return The interpolated value.
	 */
	public double interpolate(double bottom, double top, double mu)
	{
		switch(this)
		{
			case COSINE:
				mu = (1 - Math.cos(mu * Math.PI)) / 2;
			default:
				return (bottom * (1 - mu)) + (top * mu);
		}
	}
	
	/**
	 * Interpolates between the given values using this type interpolation. (top = 1, bottom = 0)
	 * @param past The value at -1.
	 * @param bottom The value at 0.
	 * @param top The value at 1.
	 * @param future The value at 2.
	 * @param mu The distance between.
	 * @return The interpolated value.
	 */
	@SuppressWarnings("incomplete-switch")
	public double interpolate(double past, double bottom, double top, double future, double mu)
	{
		if(this != CUBIC && this != CATMULL_ROM)
		{
			if(this == HERMITE)
			{
				return interpolate(past, bottom, top, future, mu, 0, 0);
			}
			return interpolate(bottom, top, mu);
		}
		double muSq = mu * mu;
		double a0;
		double a1;
		double a2;
		double a3;
		switch(this)
		{
			case CUBIC:
			{
				a0 = (future - top) - (past - bottom);
				a1 = (past - bottom) - a0;
				a2 = (top - past);
				a3 = bottom;
				return (a0 * mu * muSq) + (a1 * muSq) + (a2 * mu) + a3;
			}
			case CATMULL_ROM:
			{
				a0 = ((future / 2) - (top * 3 / 2)) - ((past / 2) - (bottom * 3 / 2));
				a1 = (past - (bottom * 5 / 2)) - ((future / 2) - (top * 2));
				a2 = (top - past) / 2;
				a3 = bottom;
				return (a0 * mu * muSq) + (a1 * muSq) + (a2 * mu) + a3;
			}
		}
		return interpolate(bottom, top, mu);//should never get down to here....
	}
	
	/**
	 * Interpolates between the given values using this type interpolation. (top = 1, bottom = 0)
	 * @param past The value at -1.
	 * @param bottom The value at 0.
	 * @param top The value at 1.
	 * @param future The value at 2.
	 * @param mu The distance between.
	 * @param tension The tension for HERMITE.
	 * @param bias The bias for HERMITE.
	 * @return The interpolated value.
	 */
	public double interpolate(double past, double bottom, double top, double future, double mu, double tension, double bias)
	{
		if(this != HERMITE)
		{
			return interpolate(past, bottom, top, future, mu);
		}
		
		double muSq = mu * mu;
		double muCu = muSq * mu;
		double tenMulti = (1 - tension) / 2;
		
		double m0 = (bottom - past) * (1 + bias) * tenMulti;
		      m0 += (top -  bottom) * (1 - bias) * tenMulti;
		double m1 = (top -  bottom) * (1 + bias) * tenMulti;
		      m1 += (future -  top) * (1 - bias) * tenMulti;
		      
		double a0 =  (2 * muCu) - (3 * muSq) + 1;
		double a1 =      (muCu) - (2 * muSq) + mu;
		double a2 =      (muCu) -     (muSq);
		double a3 = -(2 * muCu) + (3 * muSq);
		
		return (a0 * bottom) + (a1 * m0) + (a2 * m1) + (a3 * top);
	}
}
