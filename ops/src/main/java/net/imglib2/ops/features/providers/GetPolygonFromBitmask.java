package net.imglib2.ops.features.providers;

import java.awt.Polygon;

import net.imglib2.ExtendedRandomAccessibleInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.ops.features.annotations.RequiredFeature;
import net.imglib2.ops.features.providers.sources.GetBinaryMask;
import net.imglib2.ops.features.providers.sources.GetPolygon;
import net.imglib2.outofbounds.OutOfBoundsConstantValueFactory;
import net.imglib2.type.logic.BitType;

public class GetPolygonFromBitmask extends GetPolygon
{
	@RequiredFeature
	GetBinaryMask binaryMask;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Polygon Tracer";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GetPolygonFromBitmask copy()
	{
		return new GetPolygonFromBitmask();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Polygon recompute()
	{
		final RandomAccessibleInterval< BitType > ii = binaryMask.get();
		return extractPolygon( ii, new int[ ii.numDimensions() ] );
	}

	/**
	 * Extracts a polygon of a 2D binary image using the Square Tracing
	 * Algorithm (be aware of its drawbacks, e.g. if the pattern is
	 * 4-connected!)
	 * 
	 * @param img
	 *            the image, note that only the first and second dimension are
	 *            taken into account
	 * @param offset
	 *            an offset for the points to be set in the new polygon
	 * @return
	 */
	private Polygon extractPolygon( final RandomAccessibleInterval< BitType > img, final int[] offset )
	{
		final RandomAccess< BitType > cur = new ExtendedRandomAccessibleInterval< BitType, RandomAccessibleInterval< BitType >>( img, new OutOfBoundsConstantValueFactory< BitType, RandomAccessibleInterval< BitType >>( new BitType( false ) ) ).randomAccess();
		boolean start = false;
		// find the starting point
		for ( int i = 0; i < img.dimension( 0 ); i++ )
		{
			for ( int j = 0; j < img.dimension( 1 ); j++ )
			{
				cur.setPosition( i, 0 );
				cur.setPosition( j, 1 );
				if ( cur.get().get() )
				{
					cur.setPosition( i, 0 );
					cur.setPosition( j, 1 );
					start = true;
					break;
				}
			}
			if ( start )
			{
				break;
			}
		}
		int dir = 1;
		int dim = 0;
		final int[] startPos = new int[] { cur.getIntPosition( 0 ), cur.getIntPosition( 1 ) };
		final Polygon p = new Polygon();
		while ( !( ( cur.getIntPosition( 0 ) == startPos[ 0 ] ) && ( cur.getIntPosition( 1 ) == startPos[ 1 ] ) && ( dim == 0 ) && ( dir == 1 ) && !start ) )
		{
			if ( cur.get().get() )
			{
				p.addPoint( offset[ 0 ] + cur.getIntPosition( 0 ), offset[ 1 ] + cur.getIntPosition( 1 ) );
				cur.setPosition( cur.getIntPosition( dim ) - dir, dim );
				if ( ( ( dim == 1 ) && ( dir == 1 ) ) || ( ( dim == 1 ) && ( dir == -1 ) ) )
				{
					dir *= -1;
				}
			}
			else
			{
				cur.setPosition( cur.getIntPosition( dim ) + dir, dim );
				if ( ( ( dim == 0 ) && ( dir == 1 ) ) || ( ( dim == 0 ) && ( dir == -1 ) ) )
				{
					dir *= -1;
				}
			}

			dim = ( dim + 1 ) % 2;
			start = false;
		}
		return p;
	}
}
