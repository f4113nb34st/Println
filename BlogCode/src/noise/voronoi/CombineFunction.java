package noise.voronoi;

/**
 * 
 * Enum of different combine functions for Voronoi noise.
 * 
 * @author F4113nb34st
 *
 */
public enum CombineFunction
{
	/**Closest.*/
	F1, 
	/**Second Closest.*/
	F2, 
	/**Second minus First Closest.*/
	F2_F1, 
	/**Third Closest.*/
	F3, 
	/**Third minus First Closest.*/
	F3_F1, 
	/**Third minus Second Closest.*/
	F3_F2, 
	/**Third minus Second plus First Closest.*/
	F3_F2_F1;
	
	/**
	 * Returns the value to use based on the given distances.
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
			case F3_F1:
				return values[2] - values[0];
			case F3_F2:
				return values[2] - values[1];
			case F3_F2_F1:
				return values[2] - values[1] + values[0];
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
			case F3_F1:
			case F3_F2:
			case F3_F2_F1:
				return 3;
		}
	}
}
