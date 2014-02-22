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

package net.imglib2.ops.operation.randomaccessibleinterval.unary.morph;

import java.util.Arrays;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.iterator.IntervalIterator;
import net.imglib2.ops.types.ConnectedType;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.type.logic.BitType;
import net.imglib2.view.Views;

/**
 * @author Christian Dietz (University of Konstanz)
 * @author Felix Schoenenberger (University of Konstanz)
 */
public class BinaryOps
{

	private OutOfBoundsFactory< BitType, RandomAccessibleInterval< BitType >> m_factory;

	public BinaryOps( OutOfBoundsFactory< BitType, RandomAccessibleInterval< BitType >> factory )
	{
		m_factory = factory;
	}

	public RandomAccessibleInterval< BitType > erode( ConnectedType type, final RandomAccessibleInterval< BitType > r, final RandomAccessibleInterval< BitType > op, final int count )
	{
		return binaryop( type, r, op, true, count );
	}

	public RandomAccessibleInterval< BitType > dilate( ConnectedType type, final RandomAccessibleInterval< BitType > r, final RandomAccessibleInterval< BitType > op, final int count )
	{
		return binaryop( type, r, op, false, count );
	}

	private RandomAccessibleInterval< BitType > binaryop( ConnectedType type, final RandomAccessibleInterval< BitType > r, final RandomAccessibleInterval< BitType > op, final boolean erode, final int count )
	{
		long[] dim = new long[ r.numDimensions() ];
		r.dimensions( dim );
		switch ( r.numDimensions() )
		{
		case 2:
			switch ( type )
			{
			case EIGHT_CONNECTED:
				unrolled2DEightConnected( r, op, erode, count );
				break;
			case FOUR_CONNECTED:
				unrolled2DFourConnected( r, op, erode, count );
				break;
			default:
				throw new IllegalArgumentException( "Can't find ConnectionType. Please choose between for connected and eightconnected" );
			}

			break;
		default:
			switch ( type )
			{
			case EIGHT_CONNECTED:
				nDEightConnected( r, op, dim, erode, count );
				break;
			case FOUR_CONNECTED:
				nDFourConnected( r, op, dim, erode, count );
				break;
			default:
				throw new IllegalArgumentException( "Can't find ConnectionType. Please choose between for connected and eightconnected" );
			}

			break;
		}
		return r;
	}

	private void unrolled2DFourConnected( final RandomAccessibleInterval< BitType > r, final RandomAccessibleInterval< BitType > op, final boolean erode, final int count )
	{

		Cursor< BitType > resCur = Views.iterable( r ).cursor();
		Cursor< BitType > srcCur = Views.iterable( op ).localizingCursor();
		RandomAccess< BitType > srcRA = Views.extend( op, m_factory ).randomAccess();

		int c = 0;
		int[] pos = new int[ op.numDimensions() ];

		while ( srcCur.hasNext() )
		{
			srcCur.fwd();
			resCur.fwd();

			boolean t = srcCur.get().get();

			if ( erode == t )
			{
				srcRA.setPosition( srcCur );
				srcRA.localize( pos );
				c = 0;

				// first row
				srcRA.setPosition( pos[ 0 ] - 1, 0 );
				if ( srcRA.get().get() )
					c++;
				srcRA.setPosition( pos[ 0 ] + 1, 0 );
				if ( srcRA.get().get() )
					c++;

				// second row
				srcRA.setPosition( pos[ 0 ], 0 );
				srcRA.setPosition( pos[ 1 ] - 1, 1 );
				if ( srcRA.get().get() )
					c++;
				srcRA.setPosition( pos[ 1 ] + 1, 1 );
				if ( srcRA.get().get() )
					c++;

				if ( erode )
				{
					resCur.get().set( 4 - c < count );
				}
				else
				{
					resCur.get().set( c >= count );
				}
			}
			else
			{
				resCur.get().set( t );
			}
		}
	}

	private void unrolled2DEightConnected( final RandomAccessibleInterval< BitType > r, final RandomAccessibleInterval< BitType > op, final boolean erode, final int count )
	{

		Cursor< BitType > resCur = Views.flatIterable( r ).cursor();
		Cursor< BitType > srcCur = Views.flatIterable( op ).localizingCursor();
		RandomAccess< BitType > srcRA = Views.extend( op, m_factory ).randomAccess();

		int c = 0;
		int[] pos = new int[ op.numDimensions() ];

		while ( srcCur.hasNext() )
		{
			srcCur.fwd();
			resCur.fwd();

			boolean t = srcCur.get().get();

			if ( erode == t )
			{
				srcRA.setPosition( srcCur );
				srcRA.localize( pos );
				c = 0;

				// first row
				srcRA.setPosition( pos[ 1 ] - 1, 1 );
				srcRA.setPosition( pos[ 0 ] - 1, 0 );
				if ( srcRA.get().get() )
					c++;
				srcRA.setPosition( pos[ 0 ], 0 );
				if ( srcRA.get().get() )
					c++;
				srcRA.setPosition( pos[ 0 ] + 1, 0 );
				if ( srcRA.get().get() )
					c++;

				// second row
				srcRA.setPosition( pos[ 1 ], 1 );
				srcRA.setPosition( pos[ 0 ] - 1, 0 );
				if ( srcRA.get().get() )
					c++;
				srcRA.setPosition( pos[ 0 ], 0 );
				if ( srcRA.get().get() )
					c++;
				srcRA.setPosition( pos[ 0 ] + 1, 0 );
				if ( srcRA.get().get() )
					c++;

				// third row
				srcRA.setPosition( pos[ 1 ] + 1, 1 );
				srcRA.setPosition( pos[ 0 ] - 1, 0 );
				if ( srcRA.get().get() )
					c++;
				srcRA.setPosition( pos[ 0 ], 0 );
				if ( srcRA.get().get() )
					c++;
				srcRA.setPosition( pos[ 0 ] + 1, 0 );
				if ( srcRA.get().get() )
					c++;

				if ( erode )
				{
					resCur.get().set( 9 - c < count );
				}
				else
				{
					resCur.get().set( c >= count );
				}
			}
			else
			{
				resCur.get().set( t );
			}
		}
	}

	private void nDEightConnected( final RandomAccessibleInterval< BitType > res, final RandomAccessibleInterval< BitType > src, final long[] dim, final boolean erode, final int count )
	{

		RandomAccess< BitType > r = Views.extend( res, m_factory ).randomAccess();
		RandomAccess< BitType > op = Views.extend( src, m_factory ).randomAccess();

		final int kernelSize = ( int ) Math.pow( 3, dim.length );
		final int[] kernel = new int[ kernelSize ];
		int kernelIndex;
		int x0;
		int sum;
		int i;
		// Setup line cursor, points on the first pixel in each line
		final long[] ldim = dim.clone();
		ldim[ 0 ] = 1;
		IntervalIterator lineCur = new IntervalIterator( ldim );
		// Region of interest cursor, iterates over the plane with the
		// new pixel
		// values
		final long[] roi = new long[ dim.length ];
		Arrays.fill( roi, 3 );
		roi[ 0 ] = 1;
		IntervalIterator roiCur = new IntervalIterator( roi );
		while ( lineCur.hasNext() )
		{
			lineCur.fwd();
			// Initialize kernel in new line
			kernelIndex = 0;
			sum = 0;
			for ( x0 = -2; x0 < 0; x0++ )
			{
				op.setPosition( x0, 0 );
				roiCur.reset();
				while ( roiCur.hasNext() )
				{
					roiCur.fwd();
					for ( i = 1; i < dim.length; i++ )
					{
						op.setPosition( lineCur.getLongPosition( i ) + roiCur.getLongPosition( i ) - 1, i );
					}
					kernel[ kernelIndex ] = op.get().getInteger();
					sum += kernel[ kernelIndex ];
					kernelIndex = ( kernelIndex + 1 ) % kernel.length;
				}
			}
			// Go through the line
			for ( x0 = 0; x0 < dim[ 0 ]; x0++ )
			{
				op.setPosition( x0 + 1, 0 );
				roiCur.reset();
				while ( roiCur.hasNext() )
				{
					roiCur.fwd();
					for ( i = 1; i < dim.length; i++ )
					{
						op.setPosition( lineCur.getLongPosition( i ) + roiCur.getLongPosition( i ) - 1, i );
					}
					sum -= kernel[ kernelIndex ];
					kernel[ kernelIndex ] = op.get().getInteger();
					sum += kernel[ kernelIndex ];
					kernelIndex = ( kernelIndex + 1 ) % kernel.length;
				}
				r.setPosition( x0, 0 );
				for ( i = 1; i < dim.length; i++ )
				{
					r.setPosition( lineCur.getLongPosition( i ), i );
				}
				if ( erode )
				{
					r.get().set( ( kernelSize - sum ) < count );
				}
				else
				{
					r.get().set( sum >= count );
				}
			}
		}
	}

	private void nDFourConnected( final RandomAccessibleInterval< BitType > r, final RandomAccessibleInterval< BitType > op, final long[] dim, final boolean erode, final int count )
	{
		Cursor< BitType > resCur = Views.iterable( r ).cursor();
		Cursor< BitType > srcCur = Views.iterable( op ).localizingCursor();
		RandomAccess< BitType > srcRA = Views.extend( op, m_factory ).randomAccess();

		int c = 0;
		int[] pos = new int[ op.numDimensions() ];
		int kernelsize = dim.length * 2;

		while ( srcCur.hasNext() )
		{
			srcCur.fwd();
			resCur.fwd();

			boolean t = srcCur.get().get();

			if ( erode == t )
			{
				srcRA.setPosition( srcCur );
				srcRA.localize( pos );
				c = 0;
				for ( int i = 0; i < dim.length; i++ )
				{
					srcRA.setPosition( pos[ i ] - 1, i );
					if ( srcRA.get().get() )
						c++;
					srcRA.setPosition( pos[ i ] + 1, i );
					if ( srcRA.get().get() )
						c++;
					srcRA.setPosition( pos[ i ], i );
				}
				if ( erode )
				{
					resCur.get().set( kernelsize - c < count );
				}
				else
				{
					resCur.get().set( c >= count );
				}
			}
			else
			{
				resCur.get().set( t );
			}
		}
	}
}
