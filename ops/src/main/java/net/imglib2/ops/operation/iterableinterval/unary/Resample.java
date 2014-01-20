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

package net.imglib2.ops.operation.iterableinterval.unary;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealRandomAccess;
import net.imglib2.interpolation.InterpolatorFactory;
import net.imglib2.interpolation.randomaccess.LanczosInterpolatorFactory;
import net.imglib2.interpolation.randomaccess.NLinearInterpolatorFactory;
import net.imglib2.interpolation.randomaccess.NearestNeighborInterpolatorFactory;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.outofbounds.OutOfBoundsMirrorFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

/**
 * Implement more interpolators (e.g. bilinear interpolation)
 * 
 * @author Christian Dietz (University of Konstanz)
 * 
 * @param <T>
 */
@Deprecated
public class Resample< T extends RealType< T > > implements UnaryOperation< RandomAccessibleInterval< T >, RandomAccessibleInterval< T > >
{

	public enum Mode
	{
		LINEAR, NEAREST_NEIGHBOR, PERIODICAL, LANCZOS;
	}

	private final Mode m_mode;

	public Resample( Mode mode )
	{
		m_mode = mode;
	}

	@Override
	public RandomAccessibleInterval< T > compute( RandomAccessibleInterval< T > op, RandomAccessibleInterval< T > res )
	{

		InterpolatorFactory< T, RandomAccessible< T >> ifac;
		switch ( m_mode )
		{
		case LINEAR:
			ifac = new NLinearInterpolatorFactory< T >();
			break;
		case NEAREST_NEIGHBOR:
			ifac = new NearestNeighborInterpolatorFactory< T >();
			break;
		case LANCZOS:
			ifac = new LanczosInterpolatorFactory< T >();
			break;
		case PERIODICAL:
			RandomAccess< T > srcRA = Views.extendPeriodic( op ).randomAccess();
			Cursor< T > resCur = Views.iterable( res ).localizingCursor();
			while ( resCur.hasNext() )
			{
				resCur.fwd();
				srcRA.setPosition( resCur );
				resCur.get().set( srcRA.get() );
			}

			return res;
		default:
			throw new IllegalArgumentException( "Unknown mode in Resample.java" );
		}

		final RealRandomAccess< T > inter = ifac.create( Views.extend( op, new OutOfBoundsMirrorFactory< T, RandomAccessibleInterval< T > >( OutOfBoundsMirrorFactory.Boundary.SINGLE ) ) );

		final Cursor< T > c2 = Views.iterable( res ).localizingCursor();
		final float[] s = new float[ res.numDimensions() ];
		for ( int i = 0; i < s.length; i++ )
			s[ i ] = ( float ) op.dimension( i ) / res.dimension( i );
		final long[] d = new long[ res.numDimensions() ];
		while ( c2.hasNext() )
		{
			c2.fwd();
			c2.localize( d );
			for ( int i = 0; i < d.length; i++ )
			{
				inter.setPosition( s[ i ] * d[ i ], i );
			}

			c2.get().set( inter.get() );

		}

		return res;
	}

	@Override
	public UnaryOperation< RandomAccessibleInterval< T >, RandomAccessibleInterval< T > > copy()
	{
		return new Resample< T >( m_mode );
	}
}
