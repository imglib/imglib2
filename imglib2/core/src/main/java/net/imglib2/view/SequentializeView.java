/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package net.imglib2.view;

import net.imglib2.Interval;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealPositionable;
import net.imglib2.transform.Transform;
import net.imglib2.transform.integer.SequentializeTransform;

/**
 * TODO
 *
 */
public class SequentializeView< T > implements TransformedRandomAccessible< T >, RandomAccessibleInterval< T >
{
	protected final int n;

	protected final RandomAccessible< T > source;
	
	protected final SequentializeTransform transformFromSource;

	protected final long[] dimension;
	protected final long[] max;
	
	public SequentializeView( RandomAccessibleInterval< T > source, final int numDimensions )
	{
		assert numDimensions <= source.numDimensions();
		
		this.n = numDimensions;
		this.source = source;

		final int m = source.numDimensions();
		final long[] sourceDim = new long[ m ];
		source.dimensions( sourceDim );
		this.transformFromSource = new SequentializeTransform( sourceDim, n);

		this.dimension = new long[ n ];
		for ( int d = 0; d < n; ++d )
			this.dimension[ d ] = sourceDim[ d ];
		for ( int d = n; d < m; ++d )
			this.dimension[ n-1 ] *= sourceDim[ d ];
		
		this.max = new long[ n ];
		for ( int d = 0; d < n; ++d )
			this.max[ d ] = this.dimension[ d ] - 1;
	}

	@Override
	public RandomAccess< T > randomAccess()
	{
		return new SequentializeRandomAccess< T >( source.randomAccess(), transformFromSource.inverse() );
	}

	@Override
	public RandomAccess< T > randomAccess( Interval interval )
	{
		return new SequentializeRandomAccess< T >( source.randomAccess(), transformFromSource.inverse() );
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
		try { return this.dimension[ d ]; }
		catch ( ArrayIndexOutOfBoundsException e ) { return 1; }
	}
	
	@Override
	public String toString()
	{
		String className = this.getClass().getCanonicalName();
		className = className.substring( className.lastIndexOf(".") + 1, className.length());
		
		String description = className + " [" + dimension[ 0 ];
		
		for ( int i = 1; i < n; ++i )
			description += "x" + dimension[ i ];
		
		description += "]";
		
		return description;
	}

	@Override
	public double realMax( int d )
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
	public double realMin( int d )
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
	public long max( int d )
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
	public long min( int d )
	{
		return 0;
	}

	@Override
	public void min( final long[] m )
	{
		for ( int d = 0; d < n; ++d )
			m[ d ] = 0;
	}

	@Override
	public RandomAccessible< T > getSource()
	{
		return source;
	}

	@Override
	public Transform getTransformToSource()
	{
		return transformFromSource.inverse();
	}

	@Override
	public void min( Positionable m )
	{
		for ( int d = 0; d < n; ++d )
			m.setPosition( 0, d );
	}

	@Override
	public void max( Positionable m )
	{
		m.setPosition( max );
	}

	@Override
	public void realMin( RealPositionable m )
	{
		for ( int d = 0; d < n; ++d )
			m.setPosition( 0, d );
	}

	@Override
	public void realMax( RealPositionable m )
	{
		m.setPosition( max );
	}
}
