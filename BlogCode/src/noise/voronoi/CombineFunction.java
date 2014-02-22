package noise.voronoi;

public enum CombineFunction
{
	F1, F2, F2_F1, F3;
	
	/**
	 * Returns the noise value to use based on the given distances.
	 */
	public double combineFunc(double[] values)
	{
		switch(this)
		{
			default://default is also F1
				return values[0];
			case F2:	
				return values[1];
			case F2_F1:	
		        return values[1] - values[0];
			case F3:
				return values[2];
		}
	}
	
	/**
	 * Returns how many distance values must be calculated for this function.
	 */
	public int getNumDistances()
	{
		switch(this)
		{
			default://default is also F1
				return 1;
			case F2:	
			case F2_F1:
		        return 2;
			case F3:
				return 3;
		}
	}
}
