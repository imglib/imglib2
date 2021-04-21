/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2021 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
/**
 *
 */
package net.imglib2.view;

import java.util.Arrays;

import net.imglib2.Interval;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;

/**
 * {@link HyperSlicesView} is a {@link RandomAccessible} of all
 * {@link HyperSlice hyperslices}.  This implementation creates a new and
 * therefore independent {@link HyperSlice} per {@code get()} at the correct
 * position.
 *
 * @author Stephan Saalfeld &lt;saalfelds@janelia.hhmi.org&gt;
 *
 */
public class HyperSlicesView< T > implements RandomAccessible< HyperSlice< T > >
{
	final protected RandomAccessible< T > source;
	final protected int numDimensions;

	/* outside of the hyperslice */
	final protected int[] accessAxes;

	public class HyperSlicesViewRandomAccess extends Point implements RandomAccess< HyperSlice< T > >
	{
		public HyperSlicesViewRandomAccess()
		{
			super( numDimensions );
		}

		@Override
		public HyperSlice< T > get()
		{
			final long[] position = new long[ source.numDimensions() ];

			for ( int d = 0; d < numDimensions; ++d )
				position[ accessAxes[ d ] ] = getLongPosition( d );

			return new HyperSlice< T >( source, accessAxes, position );
		}

		@Override
		public HyperSlicesViewRandomAccess copy()
		{
			final HyperSlicesViewRandomAccess copy = new HyperSlicesViewRandomAccess();
			copy.setPosition( this );
			return copy;
		}

		@Override
		public HyperSlicesViewRandomAccess copyRandomAccess()
		{
			return copy();
		}
	}

	public HyperSlicesView( final RandomAccessible< T > source, final int... axes )
	{
		this.source = source;
		final int[] sortedAxes = axes.clone();
		Arrays.sort( sortedAxes );
		numDimensions = source.numDimensions() - sortedAxes.length;
		accessAxes = new int[ numDimensions ];
		for ( int d = 0, da = 0, db = 0; d < source.numDimensions(); ++d )
		{
			if ( da < sortedAxes.length && sortedAxes[ da ] == d )
				++da;
			else
				accessAxes[ db++ ] = d;
		}
	}

	@Override
	public int numDimensions()
	{
		return numDimensions;
	}

	@Override
	public HyperSlicesViewRandomAccess randomAccess()
	{
		return new HyperSlicesViewRandomAccess();
	}

	@Override
	public HyperSlicesViewRandomAccess randomAccess( final Interval interval )
	{
		/* intervals only matter inside hyperslice space as the slice itself is only
		 * one pixel thick (constant) in all outer dimensions */
		return randomAccess();
	}


}
