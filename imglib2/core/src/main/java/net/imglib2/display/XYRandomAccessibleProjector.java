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

package net.imglib2.display;

import net.imglib2.FinalInterval;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converter;

/**
 * 
 *
 * @author ImgLib2 developers
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class XYRandomAccessibleProjector< A, B > extends AbstractXYProjector< A, B >
{
	final protected RandomAccessibleInterval< B > target;
	
	public XYRandomAccessibleProjector( final RandomAccessible< A > source, final RandomAccessibleInterval< B > target, final Converter< A, B > converter )
	{
		super( source, converter );
		this.target = target;
	}

	@Override
	public void map()
	{
		for ( int d = 2; d < position.length; ++d )
			min[ d ] = max[ d ] = position[ d ];
		
		min[ 0 ] = target.min( 0 );
		min[ 1 ] = target.min( 1 );
		max[ 0 ] = target.max( 0 );
		max[ 1 ] = target.max( 1 );
		final FinalInterval sourceInterval = new FinalInterval( min, max );
		
		final long cr = -target.dimension( 0 );
		
		final RandomAccess< B > targetRandomAccess = target.randomAccess( target );
		final RandomAccess< A > sourceRandomAccess = source.randomAccess( sourceInterval );
		
		for (
				sourceRandomAccess.setPosition( min ), targetRandomAccess.setPosition( min[ 0 ], 0 ), targetRandomAccess.setPosition( min[ 1 ], 1 );
				targetRandomAccess.getLongPosition( 1 ) <= max[ 1 ];
				sourceRandomAccess.move( cr, 0 ), targetRandomAccess.move( cr, 0 ), sourceRandomAccess.fwd( 1 ), targetRandomAccess.fwd( 1 ) )
		{
			for ( ;
					targetRandomAccess.getLongPosition( 0 ) <= max[ 0 ];
					sourceRandomAccess.fwd( 0 ), targetRandomAccess.fwd( 0 ) )
				converter.convert( sourceRandomAccess.get(), targetRandomAccess.get() );
		}
	}
	
	

	@Override
	public void bck( final int d )
	{
		position[ d ] -= 1;
	}

	@Override
	public void fwd( final int d )
	{
		position[ d ] += 1;
	}

	@Override
	public void move( final int distance, final int d )
	{
		position[ d ] += distance;
	}

	@Override
	public void move( final long distance, final int d )
	{
		position[ d ] += distance;
	}

	@Override
	public void move( final Localizable localizable )
	{
		for ( int d = 0; d < position.length; ++d )
			position[ d ] += localizable.getLongPosition( d );
	}

	@Override
	public void move( final int[] p )
	{
		for ( int d = 0; d < position.length; ++d )
			position[ d ] += p[ d ];
	}

	@Override
	public void move( final long[] p )
	{
		for ( int d = 0; d < position.length; ++d )
			position[ d ] += p[ d ];
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		for ( int d = 0; d < position.length; ++d )
			position[ d ] = localizable.getLongPosition( d );
	}

	@Override
	public void setPosition( final int[] p )
	{
		for ( int d = 0; d < position.length; ++d )
			position[ d ] = p[ d ];
	}

	@Override
	public void setPosition( final long[] p )
	{
		for ( int d = 0; d < position.length; ++d )
			position[ d ] = p[ d ];
	}

	@Override
	public void setPosition( final int p, final int d )
	{
		position[ d ] = p;
	}

	@Override
	public void setPosition( final long p, final int d )
	{
		position[ d ] = p;
	}

	@Override
	public int numDimensions()
	{
		return position.length;
	}

	@Override
	public int getIntPosition( final int d )
	{
		return ( int )position[ d ];
	}

	@Override
	public long getLongPosition( final int d )
	{
		return position[ d ];
	}

	@Override
	public void localize( final int[] p )
	{
		for ( int d = 0; d < p.length; ++d )
			p[ d ] = ( int )position[ d ];
	}

	@Override
	public void localize( final long[] p )
	{
		for ( int d = 0; d < p.length; ++d )
			p[ d ] = position[ d ];
	}

	@Override
	public double getDoublePosition( final int d )
	{
		return position[ d ];
	}

	@Override
	public float getFloatPosition( final int d )
	{
		return position[ d ];
	}

	@Override
	public void localize( final float[] p )
	{
		for ( int d = 0; d < p.length; ++d )
			p[ d ] = position[ d ];
	}

	@Override
	public void localize( final double[] p )
	{
		for ( int d = 0; d < p.length; ++d )
			p[ d ] = position[ d ];
	}
}
