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

import java.util.Iterator;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.ops.operation.UnaryOutputOperation;
import net.imglib2.ops.operation.iterable.unary.Max;
import net.imglib2.ops.operation.iterable.unary.Mean;
import net.imglib2.ops.operation.iterable.unary.MedianOp;
import net.imglib2.ops.operation.iterable.unary.Min;
import net.imglib2.ops.operation.iterable.unary.StdDeviation;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

/**
 * Projects an {@link Img} in a given dimension.
 * 
 * @author Christian Dietz (University of Konstanz)
 * @author Martin Horn (University of Konstanz)
 */
public class ImgProject< T extends RealType< T >> implements UnaryOutputOperation< Img< T >, Img< T >>
{

	/**
	 * Different projection types which can be used by ImageJ
	 */
	public enum ProjectionType
	{
		MAX_INTENSITY, MEDIAN_INTENSITY, AVG_INTENSITY, MIN_INTENSITY, STD_DEVIATION;
	}

	/* Type of projection */
	private final ProjectionType m_projectionType;

	/* Dimension of projection */
	private final int m_projectionDim;

	/**
	 * Projects the pixels onto all dimensions in the direction of
	 * <code>projectionDim</code>
	 * 
	 * @param type
	 * @param imgFactory
	 * @param projectionDim
	 */
	public ImgProject( ProjectionType type, int projectionDim )
	{
		m_projectionDim = projectionDim;
		m_projectionType = type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Img< T > createEmptyOutput( Img< T > op )
	{
		/* The new dimensions of the projected image */
		long[] projectedImgDimSizes = new long[ op.numDimensions() - 1 ];
		for ( int d = 0; d < op.numDimensions(); d++ )
		{
			if ( d < m_projectionDim )
			{
				projectedImgDimSizes[ d ] = op.dimension( d );
			}
			if ( d > m_projectionDim )
			{
				projectedImgDimSizes[ d - 1 ] = op.dimension( d );
			}
		}

		/* The projected Image */
		Img< T > projectedImage = op.factory().create( projectedImgDimSizes, op.randomAccess().get().createVariable() );

		return projectedImage;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Img< T > compute( final Img< T > op, final Img< T > r )
	{

		Cursor< T > projCur = r.localizingCursor();
		final RandomAccess< T > srcRA = op.randomAccess();

		while ( projCur.hasNext() )
		{
			projCur.fwd();
			for ( int d = 0; d < op.numDimensions(); d++ )
			{
				if ( d < m_projectionDim )
				{
					srcRA.setPosition( projCur.getIntPosition( d ), d );
				}
				if ( d > m_projectionDim )
				{
					srcRA.setPosition( projCur.getIntPosition( d - 1 ), d );
				}
			}

			projCur.get().setReal( handleProjection( new Iterator< T >()
			{
				int k = -1;

				@Override
				public boolean hasNext()
				{
					return k < op.dimension( m_projectionDim ) - 1;
				}

				@Override
				public T next()
				{
					k++;
					srcRA.setPosition( k, m_projectionDim );
					return srcRA.get();
				}

				@Override
				public void remove()
				{

				}
			} ) );

		}
		return r;
	}

	/*
	 * Projection is handled according to the projection type m_d has the size
	 * same size as the amount of points which are projected
	 * 
	 * @param resultList
	 * 
	 * @param type
	 */
	private final double handleProjection( Iterator< T > iterable )
	{

		UnaryOperation< Iterator< T >, DoubleType > op;
		switch ( m_projectionType )
		{
		case AVG_INTENSITY:
			op = new Mean< T, DoubleType >();
			break;

		case MEDIAN_INTENSITY:
			op = new MedianOp< T, DoubleType >();
			break;

		case MAX_INTENSITY:
			op = new Max< T, DoubleType >();
			break;

		case MIN_INTENSITY:
			op = new Min< T, DoubleType >();
			break;

		case STD_DEVIATION:
			op = new StdDeviation< T, DoubleType >();
			break;
		default:
			throw new IllegalArgumentException( "Projection Method doesn't exist" );
		}

		return op.compute( iterable, new DoubleType() ).get();

	}

	@Override
	public UnaryOutputOperation< Img< T >, Img< T >> copy()
	{
		return new ImgProject< T >( m_projectionType, m_projectionDim );
	}

	@Override
	public Img< T > compute( Img< T > arg0 )
	{
		return compute( arg0, createEmptyOutput( arg0 ) );
	}

}
