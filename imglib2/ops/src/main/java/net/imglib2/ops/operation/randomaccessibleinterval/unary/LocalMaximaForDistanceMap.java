package net.imglib2.ops.operation.randomaccessibleinterval.unary;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.roi.RectangleRegionOfInterest;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

/**
 * Finds local maxima inside a distance map
 * 
 * @author metznerj, dietzc
 * 
 * @param <T>
 * @param <K>
 */
public class LocalMaximaForDistanceMap< T extends RealType< T >, K extends RandomAccessibleInterval< T > & IterableInterval< T >> implements UnaryOperation< K, List< long[] >>
{

	public enum NeighborhoodType
	{

		EIGHT( -1, 3 ), SIXTEEN( -2, 5 ), THIRTYTWO( -3, 7 );

		private int m_offset;

		private int m_extend;

		private NeighborhoodType( int offset, int extend )
		{
			m_offset = offset;
			m_extend = extend;
		}

		public final int getOffset()
		{
			return m_offset;
		}

		public final int getExtend()
		{
			return m_extend;
		}

	}

	/* Inital origin of the sliding window */
	private double[] m_roiOrigin;

	/* Extend of the sliding window */
	private double[] m_roiExtend;

	/* region of interest (sliding window) */
	private RectangleRegionOfInterest m_roi;

	/* Region of interest cursor */
	private Cursor< T > m_roiCursor;

	/* defined neighborhood */
	private final NeighborhoodType m_neighborhood;

	public LocalMaximaForDistanceMap( NeighborhoodType neighborhood )
	{
		m_neighborhood = neighborhood;
	}

	@Override
	public List< long[] > compute( final K src, final List< long[] > res )
	{
		final int numDims = src.numDimensions();
		if ( src.numDimensions() < 2 )
		{
			throw new IllegalArgumentException( "Image must have at least 2 dimensions" );
		}
		else if ( src.numDimensions() > 3 ) { throw new IllegalArgumentException( "Only three dimensions are allowed" ); }
		if ( m_roi == null || m_roiOrigin.length != m_roi.numDimensions() )
		{
			m_roiOrigin = new double[ numDims ];
			m_roiExtend = new double[ numDims ];

			for ( int d = 0; d < m_roiOrigin.length; d++ )
			{
				m_roiExtend[ d ] = m_neighborhood.getExtend();
			}

			m_roi = new RectangleRegionOfInterest( m_roiOrigin, m_roiExtend );
		}

		{
			final T val = src.firstElement().createVariable();
			val.setReal( 0 );
			m_roiCursor = m_roi.getIterableIntervalOverROI( Views.extendValue( src, val ) ).cursor();
		}

		final ArrayList< LocalMaxima > localMax = new ArrayList< LocalMaxima >( 10 );

		final Cursor< T > srcCursor = src.localizingCursor();
		while ( srcCursor.hasNext() )
		{
			srcCursor.fwd();
			for ( int d = 0; d < m_roiOrigin.length; d++ )
			{
				m_roiOrigin[ d ] = srcCursor.getIntPosition( d ) + m_neighborhood.getOffset();
			}
			m_roi.setOrigin( m_roiOrigin );
			boolean add = true;
			m_roiCursor.reset();
			final float p = srcCursor.get().getRealFloat();
			if ( p > 0 )
			{
				while ( m_roiCursor.hasNext() )
				{
					if ( m_roiCursor.next().getRealFloat() > p )
					{
						add = false;
						break;
					}

				}
				if ( add )
				{

					final long[] pos = new long[ numDims ];
					for ( int i = 0; i < numDims; ++i )
					{
						pos[ i ] = srcCursor.getIntPosition( i );
					}
					localMax.add( new LocalMaxima( srcCursor.get().getRealFloat(), pos ) );
				}
			}
		}
		final Comparator< LocalMaxima > comparator = new LocalMaximaComparator();
		java.util.Collections.sort( localMax, comparator );
		int l = localMax.size();
		for ( int i = 0; i < l; ++i )
		{
			final float r = localMax.get( i ).val * localMax.get( i ).val;
			if ( r > 1 )
			{
				for ( int u = i + 1; u < l; ++u )
				{
					float q = 0;
					for ( int a = 0; a < numDims; ++a )
					{
						final float d = localMax.get( u ).pos[ a ] - localMax.get( i ).pos[ a ];
						q += d * d;
					}
					if ( q < r )
					{
						localMax.remove( u );
						--l;
						--u;
					}
				}
			}
		}
		for ( final LocalMaxima lm : localMax )
		{
			res.add( lm.pos );
		}
		return res;

	}

	private class LocalMaxima
	{
		private final float val;

		private final long[] pos;

		public LocalMaxima( final float val, final long[] pos )
		{
			this.val = val;
			this.pos = pos;
		}
	}

	private class LocalMaximaComparator implements Comparator< LocalMaxima >
	{
		@Override
		public int compare( final LocalMaxima a, final LocalMaxima b )
		{
			final float pa = a.val;
			final float pb = b.val;
			if ( pa > pb )
				return -1;
			if ( pa < pb )
				return 1;
			return 0;
		}
	}

	@Override
	public UnaryOperation< K, List< long[] >> copy()
	{
		return new LocalMaximaForDistanceMap< T, K >( m_neighborhood );
	}

}
