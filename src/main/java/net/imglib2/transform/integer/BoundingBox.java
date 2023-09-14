/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2023 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.transform.integer;

import net.imglib2.FinalInterval;
import net.imglib2.Interval;

/**
 * TODO
 * 
 */
public final class BoundingBox
{
	final public int n;

	final public long[] corner1;

	final public long[] corner2;

	public BoundingBox( final int n )
	{
		this.n = n;
		this.corner1 = new long[ n ];
		this.corner2 = new long[ n ];
	}

	public BoundingBox( final long[] corner1, final long[] corner2 )
	{
		assert corner1.length == corner2.length;

		this.n = corner1.length;
		this.corner1 = corner1.clone();
		this.corner2 = corner2.clone();
	}

	public BoundingBox( final Interval interval )
	{
		this.n = interval.numDimensions();
		this.corner1 = new long[ n ];
		this.corner2 = new long[ n ];
		interval.min( corner1 );
		interval.max( corner2 );
	}

	public int numDimensions()
	{
		return n;
	}

	public void corner1( final long[] c )
	{
		assert c.length >= n;
		for ( int d = 0; d < n; ++d )
			c[ d ] = this.corner1[ d ];
	}

	public void corner2( final long[] c )
	{
		assert c.length >= n;
		for ( int d = 0; d < n; ++d )
			c[ d ] = this.corner2[ d ];
	}

	/**
	 * flip coordinates between corner1 and corner2 such that corner1 is the min
	 * of the bounding box and corner2 is the max.
	 */
	public void orderMinMax()
	{
		for ( int d = 0; d < n; ++d )
		{
			if ( corner1[ d ] > corner2[ d ] )
			{
				final long tmp = corner1[ d ];
				corner1[ d ] = corner2[ d ];
				corner2[ d ] = tmp;
			}
		}
	}

	/**
	 * @return bounding box as an interval.
	 */
	public Interval getInterval()
	{
		orderMinMax();
		return new FinalInterval( corner1, corner2 );
	}
}
