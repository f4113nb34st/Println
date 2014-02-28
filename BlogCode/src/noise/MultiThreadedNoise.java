package noise;

import util.concurrent.ThreadPool;

/**
 * 
 * Represents a NoiseGenerator that can fill NoiseArrays multi-threaded-ly.
 * 
 * @author F4113nb34st
 *
 */
public interface MultiThreadedNoise
{
	/**
	 * Fills the given NoiseArray with this noise, using multiple threads.
	 * @param array The array to fill.
	 * @param pool The thread pool to use for processing.
	 */
	public void fillMultiThreaded(NoiseArray noise, ThreadPool pool);
}
