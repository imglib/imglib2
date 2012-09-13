/*
 * ------------------------------------------------------------------------
 *
 *  Copyright (C) 2003 - 2010
 *  University of Konstanz, Germany and
 *  KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME. The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 */
package net.imglib2.ops.operation.randomaccessibleinterval.unary;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.ops.operation.randomaccessibleinterval.unary.regiongrowing.AbstractRegionGrowing;
import net.imglib2.ops.types.ConnectedType;
import net.imglib2.outofbounds.OutOfBounds;
import net.imglib2.outofbounds.OutOfBoundsConstantValueFactory;
import net.imglib2.type.Type;

/**
 * Implements a hybrid grayscale reconstruction algorithm as proposed by Luc
 * Vincent in his paper Morphological Grayscale Reconstruction in Image
 * Analysis: Application and Efficient Algorithms.<br>
 * 
 * @author muethingc, University of Konstanz
 */
public abstract class AbstractGrayscaleReconstruction< T extends Type< T >, V extends Type< V >, MASK extends RandomAccessibleInterval< T >, MARKER extends RandomAccessibleInterval< V >> implements UnaryOperation< MASK, MARKER >
{

	/**
	 * A Cursor like class, but does not confirm to Cursor from imglib.
	 * 
	 * This is needed because a Cursor from imglib2 does not allow to iterate
	 * backwards.
	 */
	protected final class Cursor< U extends Type< U >>
	{

		private final RandomAccess< U > m_ra;

		private final long[] m_breaks;

		private final long[] m_lastPos;

		private final long m_numPixel;

		private long m_count = 0;

		public Cursor( final RandomAccessibleInterval< U > ra )
		{
			m_ra = ra.randomAccess();

			m_numPixel = numPixels( ra );

			m_lastPos = new long[ ra.numDimensions() ];
			for ( int i = 0; i < m_lastPos.length; i++ )
			{
				m_lastPos[ i ] = ra.dimension( i ) - 1;
			}

			m_breaks = new long[ ra.numDimensions() ];
			ra.dimensions( m_breaks );

			for ( int i = 1; i < m_breaks.length; i++ )
			{
				m_breaks[ i ] *= m_breaks[ i - 1 ];
			}

			setToOrigin();
		}

		public void fwd()
		{
			m_count++;

			if ( m_count % m_breaks[ 0 ] == 0 )
			{

				// we checked this already, so just do it
				m_ra.setPosition( 0, 0 );
				m_ra.fwd( 1 );

				// skip the last dim, is handled by previous
				// dims
				for ( int i = 1; i < m_breaks.length - 1; i++ )
				{
					if ( m_count % m_breaks[ i ] == 0 )
					{
						m_ra.setPosition( 0, i );
						m_ra.fwd( i + 1 );
					}
				}
			}
			else
			{
				m_ra.fwd( 0 );
			}
		}

		public void bwd()
		{
			if ( m_count % m_breaks[ 0 ] == 0 )
			{

				// we check this already, so just do it
				m_ra.setPosition( m_lastPos[ 0 ], 0 );
				m_ra.bck( 1 );

				// skip the last dim, is handled by previous
				// dims
				for ( int i = 1; i < m_breaks.length - 1; i++ )
				{
					if ( m_count % m_breaks[ i ] == 0 )
					{
						m_ra.setPosition( m_lastPos[ i ], i );
						m_ra.bck( i + 1 );
					}
				}
			}
			else
			{
				m_ra.bck( 0 );
			}

			m_count--;
		}

		public void setLastPos()
		{
			m_ra.setPosition( m_lastPos );
			m_count = m_numPixel - 1;
		}

		public void setToOrigin()
		{
			long[] pos = new long[ m_ra.numDimensions() ];
			Arrays.fill( pos, 0 );
			m_ra.setPosition( pos );

			m_count = 0;
		}

		public boolean hasNextFwd()
		{
			return m_count < m_numPixel;
		}

		public boolean hasNextBwd()
		{
			return m_count > 0;
		}

		public RandomAccess< U > getRandomAccess()
		{
			return m_ra;
		}

		public U get()
		{
			return m_ra.get();
		}

		public int getIntPosition( final int d )
		{
			return m_ra.getIntPosition( d );
		}
	}

	private final ConnectedType m_connection;

	/*
	 * These hold the neighbourhood arrays, depending on the chosen
	 * ConnectedType
	 */
	private long[][] m_neighboursPlus;

	private long[][] m_neighboursMinus;

	private long[][] m_neighbours;

	private final Queue< int[] > m_queue = new LinkedList< int[] >();

	public AbstractGrayscaleReconstruction( final ConnectedType connection )
	{
		m_connection = connection;
	}

	protected AbstractGrayscaleReconstruction( final AbstractGrayscaleReconstruction< T, V, MASK, MARKER > copy )
	{
		m_connection = copy.m_connection;
	}

	private void setUpNeighbours( final int dims )
	{
		switch ( m_connection )
		{
		case FOUR_CONNECTED:
			m_neighboursPlus = get4ConNeighbourhoodPlus( dims );
			m_neighboursMinus = get4ConNeighbourhoodMinus( dims );
			m_neighbours = AbstractRegionGrowing.get4ConStructuringElement( dims );
			break;
		case EIGHT_CONNECTED:
			m_neighboursPlus = get8ConNeighbourhoodPlus( dims );
			m_neighboursMinus = get8ConNeighbourhoodMinus( dims );
			m_neighbours = AbstractRegionGrowing.get8ConStructuringElement( dims );
			break;
		}
	}

	@Override
	public MARKER compute( MASK input, MARKER output )
	{
		setUpNeighbours( input.numDimensions() );

		// OutOfBounds for marker
		V zeroV = output.randomAccess().get().createVariable();
		zeroV.set( getVMinValue( zeroV ) );
		OutOfBounds< V > marker = new OutOfBoundsConstantValueFactory< V, RandomAccessibleInterval< V >>( zeroV ).create( output );

		Cursor< V > cur = new Cursor< V >( output );

		// OutOfBounds for mask
		T zeroT = input.randomAccess().get().createVariable();
		zeroT.set( getTMinValue( zeroT ) );
		OutOfBounds< T > mask = new OutOfBoundsConstantValueFactory< T, RandomAccessibleInterval< T >>( zeroT ).create( input );

		scanInRasterOrder( cur, marker, mask );
		scanInAntiRasterOrder( cur, marker, mask );

		propagate( marker, mask, m_neighbours );

		return output;
	}

	private void scanInAntiRasterOrder( Cursor< V > cur, RandomAccess< V > marker, RandomAccess< T > mask )
	{
		assert cur != null;
		assert marker != null;
		assert mask != null;

		cur.setLastPos();

		while ( cur.hasNextBwd() )
		{

			cur.get().set( checkAroundCursor( cur, marker, mask, m_neighboursMinus ) );
			checkPixels( cur, marker, mask, m_neighboursMinus );
			// this is NO imglib cursor, so bwd must be the last
			// call
			cur.bwd();
		}
	}

	private void scanInRasterOrder( Cursor< V > cur, RandomAccess< V > marker, RandomAccess< T > mask )
	{
		assert cur != null;
		assert marker != null;
		assert mask != null;

		cur.setToOrigin();

		while ( cur.hasNextFwd() )
		{
			cur.get().set( checkAroundCursor( cur, marker, mask, m_neighboursPlus ) );

			// this is NO imglib cursor, so fwd must be the last
			// call
			cur.fwd();
		}
	}

	private V checkAroundCursor( Cursor< V > cur, RandomAccess< V > marker, RandomAccess< T > mask, long[][] strucElement )
	{
		V val = cur.get().copy();

		for ( long[] e : strucElement )
		{

			for ( int i = 0; i < e.length; i++ )
			{
				marker.setPosition( cur.getIntPosition( i ) + e[ i ], i );
			}

			val = morphOp( val.copy(), marker.get().copy() );
		}

		mask.setPosition( cur.getRandomAccess() );
		val = pointwiseOp( val, mask.get() );

		return val;
	}

	private void checkPixels( Cursor< V > cur, RandomAccess< V > marker, RandomAccess< T > mask, long[][] strucElement )
	{
		V p = cur.get().copy();

		for ( long[] e : strucElement )
		{

			for ( int i = 0; i < e.length; i++ )
			{
				marker.setPosition( cur.getIntPosition( i ) + e[ i ], i );
				mask.setPosition( cur.getIntPosition( i ) + e[ i ], i );
			}

			V q = marker.get().copy();
			T i = mask.get().copy();

			if ( checkPixelAddToQueue( p, q, i ) )
			{
				addToQueue( cur.getRandomAccess() );
				return;
			}
		}
	}

	protected void propagate( RandomAccess< V > marker, RandomAccess< T > mask, long[][] strucElement )
	{
		while ( !m_queue.isEmpty() )
		{
			int[] p = m_queue.poll();

			marker.setPosition( p );
			V val = marker.get().copy();

			for ( long[] e : strucElement )
			{
				for ( int i = 0; i < e.length; i++ )
				{
					marker.setPosition( p[ i ] + e[ i ], i );
					mask.setPosition( p[ i ] + e[ i ], i );
				}

				V q = marker.get().copy();
				T i = mask.get().copy();

				if ( checkPixelFromQueue( val, q, i ) )
				{
					marker.get().set( pointwiseOp( val, i ) );
					addToQueue( marker );
				}
			}
		}

	}

	/*
	 * These functions implement the kind of algorithm (by erosion / dilation)
	 */
	protected abstract boolean checkPixelFromQueue( final V p, final V q, final T i );

	protected abstract boolean checkPixelAddToQueue( final V p, final V q, final T i );

	protected abstract V morphOp( final V a, final V b );

	protected abstract V pointwiseOp( final V a, final T b );

	protected abstract V getVMinValue( final V var );

	protected abstract T getTMinValue( final T var );

	protected final void addToQueue( RandomAccess< V > ra )
	{
		int[] pos = new int[ ra.numDimensions() ];
		ra.localize( pos );
		m_queue.add( pos );
	}

	private long[][] get4ConNeighbourhoodPlus( int dimensions )
	{
		assert dimensions > 1;

		return get4ConNeighbourhood( dimensions, -1 );
	}

	private long[][] get4ConNeighbourhoodMinus( int dimensions )
	{
		assert dimensions > 1;

		return get4ConNeighbourhood( dimensions, 1 );
	}

	private long[][] get4ConNeighbourhood( int dimensions, int step )
	{
		assert dimensions > 1;
		assert step == 1 || step == -1;

		int nElements = dimensions;

		long[][] result = new long[ nElements ][ dimensions ];
		for ( int d = 0; d < dimensions; d++ )
		{
			result[ d ] = new long[ dimensions ];
			result[ d ][ d ] = step;
		}
		return result;
	}

	private long[][] get8ConNeighbourhoodPlus( int dimensions )
	{
		assert dimensions > 1;

		long[][] result = prepareStructElement( dimensions );

		long[] position = new long[ dimensions ];
		Arrays.fill( position, -1 );

		for ( int i = 0; i < result.length; i++ )
		{
			System.arraycopy( position, 0, result[ i ], 0, dimensions );

			/*
			 * Special case - skip the center element.
			 */
			if ( i == result.length / 2 - 1 )
			{
				position[ 0 ] += 2;
			}
			else
			{
				position = nextPosition( position );
			}
		}
		return result;
	}

	private long[][] get8ConNeighbourhoodMinus( int dimensions )
	{
		assert dimensions > 1;

		long[][] result = prepareStructElement( dimensions );
		long[] position = new long[ dimensions ];

		Arrays.fill( position, 0 );
		position[ 0 ] = 1;

		for ( int i = 0; i < result.length; i++ )
		{
			System.arraycopy( position, 0, result[ i ], 0, dimensions );
			position = nextPosition( position );
		}
		return result;
	}

	private long[][] prepareStructElement( int dims )
	{
		assert dims > 1;

		int nElements = 1;
		for ( int i = 0; i < dims; i++ )
			nElements *= 3;
		nElements = ( nElements - 1 ) / 2;

		return new long[ nElements ][ dims ];
	}

	private long[] nextPosition( final long[] pos )
	{
		assert pos != null;

		for ( int j = 0; j < pos.length; j++ )
		{
			if ( pos[ j ] == 1 )
			{
				pos[ j ] = -1;
			}
			else
			{
				pos[ j ]++;
				break;
			}
		}

		return pos;
	}

	private long numPixels( final Interval i )
	{
		assert i != null;

		long[] dims = new long[ i.numDimensions() ];
		i.dimensions( dims );

		long acc = 1;
		for ( long l : dims )
		{
			acc *= l;
		}

		return acc;
	}
}
