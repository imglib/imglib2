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

package net.imglib2.ops.operation.img.unary;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.ops.operation.UnaryOutputOperation;
import net.imglib2.type.Type;
import net.imglib2.view.Views;

/**
 * 
 * @author Martin Horn (University of Konstanz)
 */
public class ImgRotate2D< T extends Type< T > & Comparable< T >> implements UnaryOutputOperation< Img< T >, Img< T >>
{

	private final double m_angle;

	private final int m_dimIdx1;

	private final int m_dimIdx2;

	private final boolean m_keepSize;

	private final T m_outOfBoundsType;

	private final long[] m_center;

	/**
	 * @param angle
	 *            the angle (radian)
	 * @param dimIdx1
	 *            the dimension index to rotate
	 * @param dimIdx2
	 *            the second dimension to rotate
	 * @param keepSize
	 *            if true, the result image will have the same size as the
	 *            source image, if false, the result is a 2D image with changed
	 *            dimension sizes
	 * @param outOfBoundsType
	 *            the value too be used for undefined pixels (i.e. pixel
	 *            positions in the result image which are not existent in the
	 *            source image)
	 * @param center
	 *            the rotation center, if <code>null</code> the image center
	 *            will be used
	 */
	public ImgRotate2D( double angle, int dimIdx1, int dimIdx2, boolean keepSize, T outOfBoundsType, long[] center )
	{
		m_angle = angle;
		m_dimIdx1 = dimIdx1;
		m_dimIdx2 = dimIdx2;
		m_keepSize = keepSize;
		m_outOfBoundsType = outOfBoundsType;
		m_center = center;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Img< T > createEmptyOutput( Img< T > op )
	{
		if ( m_keepSize )
		{
			return op.factory().create( op, op.randomAccess().get().createVariable() );
		}
		// rotate all for egde points and take the maximum
		// coordinate to
		// determine the new image size
		long[] min = new long[ op.numDimensions() ];
		long[] max = new long[ op.numDimensions() ];

		op.min( min );
		op.max( max );
		min[ m_dimIdx1 ] = min[ m_dimIdx2 ] = Long.MAX_VALUE;
		max[ m_dimIdx1 ] = max[ m_dimIdx2 ] = Long.MIN_VALUE;

		double[] center = calcCenter( op );

		double x;
		double y;

		for ( Long orgX : new long[] { 0, op.max( m_dimIdx1 ) } )
		{
			for ( Long orgY : new long[] { 0, op.max( m_dimIdx2 ) } )
			{
				x = ( orgX - center[ m_dimIdx1 ] ) * Math.cos( m_angle ) - ( orgY - center[ m_dimIdx2 ] ) * Math.sin( m_angle );

				y = ( orgX - center[ m_dimIdx1 ] ) * Math.sin( m_angle ) + ( orgY - center[ m_dimIdx2 ] ) * Math.cos( m_angle );
				min[ m_dimIdx1 ] = ( int ) Math.round( Math.min( x, min[ m_dimIdx1 ] ) );
				min[ m_dimIdx2 ] = ( int ) Math.round( Math.min( y, min[ m_dimIdx2 ] ) );
				max[ m_dimIdx1 ] = ( int ) Math.round( Math.max( x, max[ m_dimIdx1 ] ) );
				max[ m_dimIdx2 ] = ( int ) Math.round( Math.max( y, max[ m_dimIdx2 ] ) );
			}
		}

		long[] dims = new long[ min.length ];
		for ( int i = 0; i < dims.length; i++ )
		{
			dims[ i ] = max[ i ] - min[ i ];
		}

		return op.factory().create( new FinalInterval( dims ), op.randomAccess().get().createVariable() );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Img< T > compute( Img< T > op, Img< T > r )
	{
		RandomAccess< T > srcRA = Views.extendValue( op, m_outOfBoundsType ).randomAccess();

		Cursor< T > resCur = r.localizingCursor();

		double[] srcCenter = calcCenter( op );
		double[] resCenter = new double[ op.numDimensions() ];
		for ( int i = 0; i < resCenter.length; i++ )
		{
			resCenter[ i ] = srcCenter[ i ] * ( r.dimension( i ) / ( double ) op.dimension( i ) );
		}

		while ( resCur.hasNext() )
		{
			resCur.fwd();

			double x = ( resCur.getDoublePosition( m_dimIdx1 ) - resCenter[ m_dimIdx1 ] ) * Math.cos( m_angle ) - ( resCur.getDoublePosition( m_dimIdx2 ) - resCenter[ m_dimIdx2 ] ) * Math.sin( m_angle ) + srcCenter[ m_dimIdx1 ];
			double y = ( resCur.getDoublePosition( m_dimIdx1 ) - resCenter[ m_dimIdx1 ] ) * Math.sin( m_angle ) + ( resCur.getDoublePosition( m_dimIdx2 ) - resCenter[ m_dimIdx2 ] ) * Math.cos( m_angle ) + srcCenter[ m_dimIdx2 ];

			srcRA.setPosition( ( int ) Math.round( x ), m_dimIdx1 );
			srcRA.setPosition( ( int ) Math.round( y ), m_dimIdx2 );
			for ( int i = 0; i < op.numDimensions(); i++ )
			{
				if ( i != m_dimIdx1 && i != m_dimIdx2 )
				{
					srcRA.setPosition( resCur.getIntPosition( i ), i );
				}
			}

			resCur.get().set( srcRA.get() );

		}
		return r;

	}

	private double[] calcCenter( Interval interval )
	{
		double[] center = new double[ interval.numDimensions() ];
		if ( m_center == null )
		{
			for ( int i = 0; i < center.length; i++ )
			{
				center[ i ] = interval.dimension( i ) / 2.0;
			}
		}
		else
		{
			for ( int i = 0; i < center.length; i++ )
			{
				center[ i ] = m_center[ i ];
			}
		}

		return center;
	}

	@Override
	public UnaryOutputOperation< Img< T >, Img< T >> copy()
	{
		return new ImgRotate2D< T >( m_angle, m_dimIdx1, m_dimIdx2, m_keepSize, m_outOfBoundsType, m_center );
	}

	@Override
	public Img< T > compute( Img< T > arg0 )
	{
		return compute( arg0, createEmptyOutput( arg0 ) );
	}

}
