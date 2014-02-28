package util.concurrent;

import java.util.concurrent.locks.*;

public class ThreadBarrier
{
	private final ReentrantLock lock = new ReentrantLock();
	private final Condition trip = lock.newCondition();

	public void trip()
	{
		final ReentrantLock lock = this.lock;
		lock.lock();
		try
		{
			trip.signalAll();
		}finally
		{
			lock.unlock();
		}
	}

	public void waitFor()
	{
		final ReentrantLock lock = this.lock;
		lock.lock();
		try
		{
			try
			{
				trip.await();
			} catch(InterruptedException ex)
			{
				ex.printStackTrace();
			}
		} finally
		{
			lock.unlock();
		}
	}
}