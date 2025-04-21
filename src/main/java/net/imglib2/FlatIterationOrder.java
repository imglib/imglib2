/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2025 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2;

import java.util.ArrayList;
import java.util.List;

/**
 * A flat iteration order on an {@link IterableInterval}. Flat iteration order
 * means that cursors iterate line by line, plane by plane, etc. For instance a
 * 3D interval ranging from <em>(0,0,0)</em> to <em>(1,1,1)</em> is iterated
 * like
 * <em>(0,0,0), (1,0,0), (0,1,0), (1,1,0), (0,0,1), (1,0,1), (0,1,1), (1,1,1)</em>
 * 
 * 
 * 
 * @author Tobias Pietzsch
 * @author Christian Dietz
 */
public class FlatIterationOrder
{
	private final Interval interval;

	public FlatIterationOrder( final Interval interval )
	{
		this.interval = interval;
	}

	/**
	 * Two {@link Interval}s are considered to have same iteration order if two
	 * {@link Cursor}s return the same position in each iteration step,
	 * excluding dimensions of size 1.
	 * 
	 * <p>
	 * In some cases the equals method is too restrictive, i.e. we have false
	 * negatives: e.g., both objects must be instances of
	 * {@link FlatIterationOrder} in order to be reported as equal.
	 * </p>
	 * 
	 * <p>
	 * TODO: consider improving this definition
	 * </p>
	 * 
	 * @return true, if obj is a compatible {@link FlatIterationOrder}.
	 */
	@Override
	public boolean equals( final Object obj )
	{

		if ( !( obj instanceof FlatIterationOrder ) )
			return false;

		final Interval i = ( ( FlatIterationOrder ) obj ).interval;

		final List< Integer > l1 = validIndices( i );
		final List< Integer > l2 = validIndices( interval );

		// if the number of valid dims diverges, the intervals can't have same
		// iteration order
		if ( l1.size() != l2.size() )
			return false;

		for ( int d = 0; d < l1.size(); d++ )
		{
			if ( i.min( l1.get( d ) ) != interval.min( l2.get( d ) )
					|| i.dimension( l1.get( d ) ) != interval.dimension( l2.get( d ) ) )
				return false;
		}

		return true;
	}

	// create a list with all valid dimension indices.
	// a dimension is considered to be valid iff its larger than 1.
	private List< Integer > validIndices( final Interval i )
	{
		final List< Integer > indices = new ArrayList< Integer >( i.numDimensions() );
		for ( int j = 0; j < i.numDimensions(); j++ )
		{
			if ( i.dimension( j ) > 1 )
				indices.add( j );
		}
		return indices;
	}
}
