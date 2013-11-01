package net.imglib2.ops.features.geometric;

import net.imglib2.Cursor;
import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.RequiredFeature;
import net.imglib2.ops.features.providers.GetLocalizingCursor;
import net.imglib2.type.numeric.real.DoubleType;

public class EccentricityND extends AbstractFeature< DoubleType >
{

	@RequiredFeature
	GetLocalizingCursor< ? > cursorGet;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DoubleType recompute()
	{
		Cursor< ? > cursor = cursorGet.get();

		final int d = cursor.numDimensions();
		final long[] c1 = new long[ d ];
		final long[] c2 = new long[ d ];

		for ( int i = 0; i < d; i++ )
		{
			c1[ i ] = Integer.MAX_VALUE;
			c2[ i ] = -Integer.MAX_VALUE;
		}

		// get corners of bounding box
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			for ( int dim = 0; dim < d; dim++ )
			{
				int pos = cursor.getIntPosition( dim );
				c1[ dim ] = ( c1[ dim ] > pos ) ? pos : c1[ dim ];
				c2[ dim ] = ( c2[ dim ] < pos ) ? pos : c2[ dim ];
			}
		}

		long[] length = new long[ d ];
		for ( int dim = 0; dim < 2; dim++ )
		{
			length[ dim ] = Math.abs( c1[ dim ] - c2[ dim ] );
		}

		double res = 0;
		if ( length[ 0 ] > length[ 1 ] )
		{
			res = length[ 0 ] / length[ 1 ];
		}
		else
		{
			res = length[ 1 ] / length[ 0 ];
		}

		return new DoubleType( res );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Eccentricity N-D";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EccentricityND copy()
	{
		return new EccentricityND();
	}

}
