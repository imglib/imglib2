/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2020 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.img;

import java.util.Iterator;

import net.imglib2.Interval;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.RealPositionable;

/**
 * TODO
 * 
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public abstract class AbstractImg< T > implements Img< T >
{
	final protected int n;

	protected long numPixels;

	protected final long[] dimension;

	protected final long[] max;

	public AbstractImg( final long[] size )
	{
		this.n = size.length;

		this.numPixels = numElements( size );

		this.dimension = size.clone();
		max = new long[ size.length ];
		for ( int i = 0; i < size.length; ++i )
			max[ i ] = size[ i ] - 1;
	}

	@Override
	public Iterator< T > iterator()
	{
		return cursor();
	}

	@Override
	public T firstElement()
	{
		return cursor().next();
	}

	public static long numElements( final long[] dim )
	{
		long numPixels = 1;

		for ( int i = 0; i < dim.length; ++i )
			numPixels *= dim[ i ];

		return numPixels;
	}

	@Override
	public int numDimensions()
	{
		return n;
	}

	@Override
	public void dimensions( final long[] s )
	{
		for ( int i = 0; i < n; ++i )
			s[ i ] = dimension[ i ];
	}

	@Override
	public long dimension( final int d )
	{
		try
		{
			return this.dimension[ d ];
		}
		catch ( final ArrayIndexOutOfBoundsException e )
		{
			return 1;
		}
	}

	@Override
	public long size()
	{
		return numPixels;
	}

	@Override
	public String toString()
	{
		String className = this.getClass().getCanonicalName();
		className = className.substring( className.lastIndexOf( "." ) + 1, className.length() );

		String description = className + " [" + dimension[ 0 ];

		for ( int i = 1; i < n; ++i )
			description += "x" + dimension[ i ];

		description += "]";

		return description;
	}

	@Override
	public double realMax( final int d )
	{
		return max[ d ];
	}

	@Override
	public void realMax( final double[] m )
	{
		for ( int d = 0; d < n; ++d )
			m[ d ] = max[ d ];
	}

	@Override
	public void realMax( final RealPositionable m )
	{
		m.setPosition( max );
	}

	@Override
	public double realMin( final int d )
	{
		return 0;
	}

	@Override
	public void realMin( final double[] m )
	{
		for ( int d = 0; d < n; ++d )
			m[ d ] = 0;
	}

	@Override
	public void realMin( final RealPositionable m )
	{
		for ( int d = 0; d < n; ++d )
			m.setPosition( 0, d );
	}

	@Override
	public long max( final int d )
	{
		return max[ d ];
	}

	@Override
	public void max( final long[] m )
	{
		for ( int d = 0; d < n; ++d )
			m[ d ] = max[ d ];
	}

	@Override
	public void max( final Positionable m )
	{
		m.setPosition( max );
	}

	@Override
	public void min( final long[] m )
	{
		for ( int d = 0; d < n; ++d )
			m[ d ] = 0;
	}

	@Override
	public long min( final int d )
	{
		return 0;
	}

	@Override
	public void min( final Positionable m )
	{
		for ( int d = 0; d < n; ++d )
			m.setPosition( 0, d );
	}

	@Override
	public RandomAccess< T > randomAccess( final Interval interval )
	{
		return randomAccess();
	}

//	@Override
//	public OrthoSliceIterator< T > createOrthoSliceIterator( final Image< T > image, final int x, final int y, final int[] position )
//	{
//		return new OrthoSliceIterator< T >( image, x, y, position );
//	}
}
