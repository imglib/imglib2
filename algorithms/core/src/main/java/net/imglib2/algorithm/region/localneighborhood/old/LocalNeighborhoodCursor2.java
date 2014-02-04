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

package net.imglib2.algorithm.region.localneighborhood.old;

import net.imglib2.AbstractCursor;
import net.imglib2.RandomAccess;

/**
 * Iterates all pixels in a 3 by 3 by .... by 3 neighborhood of a certain
 * location but skipping the central pixel
 * 
 * @param <T>
 * 
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Benjamin Schmid
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class LocalNeighborhoodCursor2< T > extends AbstractCursor< T >
{
	final RandomAccess< T > source;

	private final long[] center;

	private final long[] min;

	private final long[] max;

	private final long span;

	private final long maxCount;

	private final long midCount;

	private long count;

	public LocalNeighborhoodCursor2( final RandomAccess< T > source, final long[] center, final long span )
	{
		super( source.numDimensions() );
		this.source = source;
		this.center = center;
		max = new long[ n ];
		min = new long[ n ];
		this.span = span;
		maxCount = ( long ) Math.pow( span + 1 + span, n );
		midCount = maxCount / 2 + 1;
		reset();
	}

	public LocalNeighborhoodCursor2( final RandomAccess< T > source, final long[] center )
	{
		this( source, center, 1 );
	}

	protected LocalNeighborhoodCursor2( final LocalNeighborhoodCursor2< T > c )
	{
		super( c.numDimensions() );
		this.source = c.source.copyRandomAccess();
		this.center = c.center;
		max = c.max.clone();
		min = c.min.clone();
		span = c.span;
		maxCount = c.maxCount;
		midCount = c.midCount;
	}

	@Override
	public T get()
	{
		return source.get();
	}

	@Override
	public void fwd()
	{
		source.fwd( 0 );
		if ( source.getLongPosition( 0 ) > max[ 0 ] )
		{
			source.setPosition( min[ 0 ], 0 );
			for ( int d = 1; d < n; ++d )
			{
				source.fwd( d );
				if ( source.getLongPosition( d ) > max[ d ] )
					source.setPosition( min[ d ], d );
				else
					break;
			}
		}
		if ( ++count == midCount )
			fwd();
	}

	@Override
	public void reset()
	{
		for ( int d = 0; d < n; ++d )
		{
			min[ d ] = center[ d ] - span;
			max[ d ] = center[ d ] + span;
		}
		source.setPosition( min );
		source.bck( 0 );
		count = 0;
	}

	@Override
	public boolean hasNext()
	{
		return count < maxCount;
	}

	@Override
	public float getFloatPosition( final int d )
	{
		return source.getFloatPosition( d );
	}

	@Override
	public double getDoublePosition( final int d )
	{
		return source.getDoublePosition( d );
	}

	@Override
	public int getIntPosition( final int d )
	{
		return source.getIntPosition( d );
	}

	@Override
	public long getLongPosition( final int d )
	{
		return source.getLongPosition( d );
	}

	@Override
	public void localize( final long[] position )
	{
		source.localize( position );
	}

	@Override
	public void localize( final float[] position )
	{
		source.localize( position );
	}

	@Override
	public void localize( final double[] position )
	{
		source.localize( position );
	}

	@Override
	public void localize( final int[] position )
	{
		source.localize( position );
	}

	@Override
	public LocalNeighborhoodCursor2< T > copy()
	{
		return new LocalNeighborhoodCursor2< T >( this );
	}

	@Override
	public LocalNeighborhoodCursor2< T > copyCursor()
	{
		return copy();
	}
}
