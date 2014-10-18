/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package net.imglib2.util;

/**
 * {@link RealSum} implements a method to reduce numerical instabilities when
 * summing up a very large number of double precision numbers. Numerical
 * problems occur when a small number is added to an already very large sum. In
 * such case, the reduced accuracy of the very large number may lead to the
 * small number being entirely ignored. The method here stores and updates
 * intermediate sums for all power of two elements such that the final sum can
 * be generated from intermediate sums that result from equal number of
 * summands.
 * 
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class RealSum
{
	protected boolean[] flags;

	protected double[] sums;

	/**
	 * Create a new {@link RealSum}. The fields for intermediate sums is
	 * initialized with a single element and expanded on demand as new elements
	 * are added.
	 */
	public RealSum()
	{
		flags = new boolean[ 1 ];
		sums = new double[ 1 ];
	}

	/**
	 * Create a new {@link RealSum}. The fields for intermediate sums is
	 * initialized with a given number of elements and will only be expanded on
	 * demand as new elements are added and the number of existing elements is
	 * not sufficient. This may be faster in cases where the required number of
	 * elements is known in prior.
	 * 
	 * @param capacity
	 */
	public RealSum( final int capacity )
	{
		final int ldu = Util.ldu( capacity ) + 1;
		flags = new boolean[ ldu ];
		sums = new double[ ldu ];
	}

	/**
	 * Get the current sum by summing up all intermediate sums. Do not call this
	 * method repeatedly when the sum has not changed.
	 */
	final public double getSum()
	{
		double sum = 0;
		for ( final double s : sums )
			sum += s;

		return sum;
	}

	final protected void expand( final double s )
	{
		final double[] oldSums = sums;
		sums = new double[ oldSums.length + 1 ];
		System.arraycopy( oldSums, 0, sums, 0, oldSums.length );
		sums[ oldSums.length ] = s;

		final boolean[] oldFlags = flags;
		flags = new boolean[ sums.length ];
		System.arraycopy( oldFlags, 0, flags, 0, oldFlags.length );
		flags[ oldSums.length ] = true;
	}

	/**
	 * Add an element to the sum. All intermediate sums are updated and the
	 * capacity is increased on demand.
	 * 
	 * @param a
	 *            the summand to be added
	 */
	final public void add( final double a )
	{
		int i = 0;
		double s = a;
		try
		{
			while ( flags[ i ] )
			{
				flags[ i ] = false;
				s += sums[ i ];
				sums[ i ] = 0.0;
				++i;
			}
			flags[ i ] = true;
			sums[ i ] = s;
			return;
		}
		catch ( final IndexOutOfBoundsException e )
		{
			expand( s );
		}
	}
}
