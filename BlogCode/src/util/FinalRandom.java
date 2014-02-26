/*
 * Copyright 1995-2007 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */
package util;

/**
 * 
 * Copy of java.util.Random that will never change.
 * 
 * @author Sun
 *
 */
public class FinalRandom
{
	/**
	 * The internal state associated with this pseudorandom number generator.
	 * (The specs for the methods in this class describe the ongoing
	 * computation of this value.)
	 */
	private long seed = 0;
	private final static long multiplier = 0x5DEECE66DL;
	private final static long addend = 0xBL;
	private final static long mask = (1L << 48) - 1;

	public void setSeed(long s)
	{
		seed = (s ^ multiplier) & mask;
	}
	
	private int next(int bits)
	{
		seed = (seed * multiplier + addend) & mask;
		return (int)(seed >>> (48 - bits));
	}

	public int nextInt()
	{
		return next(32);
	}

	public long nextLong()
	{
		// it's okay that the bottom word remains signed.
		return ((long)(next(32)) << 32) + next(32);
	}

	public boolean nextBoolean()
	{
		return next(1) != 0;
	}
	
	public float nextFloat() 
	{
		return next(24) / ((float)(1 << 24));
	}
	
	public double nextDouble()
	{
		return (((long)(next(26)) << 27) + next(27)) / (double)(1L << 53);
	}
}
