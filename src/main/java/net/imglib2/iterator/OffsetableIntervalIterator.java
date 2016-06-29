/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
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

package net.imglib2.iterator;

import net.imglib2.Interval;

/**
 * A {@link IntervalIterator} that has an adjustable offset
 * 
 * 
 * @author Stephan Preibisch
 */
public class OffsetableIntervalIterator extends IntervalIterator
{
	public OffsetableIntervalIterator( final long[] dimensions )
	{
		super( dimensions );
	}

	public OffsetableIntervalIterator( final int[] dimensions )
	{
		super( dimensions );
	}

	public OffsetableIntervalIterator( final long[] min, final long[] max )
	{
		super( min, max );
	}

	public OffsetableIntervalIterator( final int[] min, final int[] max )
	{
		super( min, max );
	}

	public OffsetableIntervalIterator( final Interval interval )
	{
		super( interval );
	}

	/**
	 * Adjust the offset and reset the iterator
	 * 
	 * @param min
	 *            - new offset
	 */
	public void setMin( final int[] min )
	{
		for ( int d = 0; d < n; ++d )
		{
			this.min[ d ] = min[ d ];
			this.max[ d ] = this.dimensions[ d ] + min[ d ] - 1;
		}

		reset();
	}

	/**
	 * Adjust the offset and reset the iterator
	 * 
	 * @param min
	 *            - new offset
	 */
	public void setMin( final long[] min )
	{
		for ( int d = 0; d < n; ++d )
		{
			this.min[ d ] = min[ d ];
			this.max[ d ] = this.dimensions[ d ] + min[ d ] - 1;
		}

		reset();
	}
}
