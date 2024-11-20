/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2024 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
package net.imglib2.blocks;

import net.imglib2.Interval;
import net.imglib2.util.Util;

import java.util.Arrays;

import static net.imglib2.util.Util.safeInt;

/**
 * An {@code Interval} where dimensions are {@code int[]}.
 * <p>
 * Used internally by {@code PrimitiveBlocks} and {@code BlockProcessor}.
 */
public final class BlockInterval implements Interval
{
	public BlockInterval( final int numDimensions )
	{
		this( new long[ numDimensions ], new int[ numDimensions ] );
	}

	public static BlockInterval wrap( final long[] min, final int[] size )
	{
		return new BlockInterval( min, size );
	}

	/**
	 * Return {@code interval} if it is a {@code BlockInterval}.
	 * Otherwise, copy into a new {@link BlockInterval}.
	 */
	public static BlockInterval asBlockInterval( final Interval interval )
	{
		return interval instanceof BlockInterval
				? ( BlockInterval ) interval
				: new BlockInterval( interval );
	}

	private final long[] min;

	private final int[] size;

	private BlockInterval( final long[] min, final int[] size )
	{
		this.min = min;
		this.size = size;
	}

	private BlockInterval( final Interval interval )
	{
		this( interval.numDimensions() );
		interval.min( min );
		Arrays.setAll( size, d -> safeInt( interval.dimension( d ) ) );
	}

	public void setFrom( Interval interval )
	{
		final int n = numDimensions();
		if ( n != interval.numDimensions() )
		{
			throw new IllegalArgumentException( "Interval dimensions mismatch" );
		}
		if ( interval instanceof BlockInterval )
		{
			System.arraycopy( ( ( BlockInterval ) interval ).min, 0, min, 0, n );
			System.arraycopy( ( ( BlockInterval ) interval ).size, 0, size, 0, n );
		}
		for ( int d = 0; d < n; ++d )
		{
			min[ d ] = interval.min( d );
			size[ d ] = Util.safeInt( interval.dimension( d ) );
		}
	}

	/**
	 * This returns the internal {@code long[] min}.
	 * Modifications are reflected in this interval!
	 *
	 * @return the internal {@code long[]} storing the min of this interval.
	 */
	public long[] min()
	{
		return min;
	}

	/**
	 * This returns the internal {@code int[] dimensions}.
	 * Modifications are reflected in this interval!
	 *
	 * @return the internal {@code int[]} storing the dimensions of this interval.
	 */
	public int[] size()
	{
		return size;
	}

	@Override
	public int numDimensions()
	{
		return size.length;
	}

	@Override
	public long min( final int d )
	{
		return min[ d ];
	}

	@Override
	public long max( final int d )
	{
		return min[ d ] + size[ d ] - 1;
	}

	@Override
	public long dimension( final int d )
	{
		return size[ d ];
	}
}
