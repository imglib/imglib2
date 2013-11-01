package net.imglib2.ops.features.providers;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.ops.features.annotations.RequiredFeature;
import net.imglib2.ops.features.providers.sources.GetIterableInterval;
import net.imglib2.ops.features.providers.sources.GetRandomAccessibleInterval;
import net.imglib2.type.NativeType;

public class GetRandomAccessibleIntervalFromIterableInterval< T extends NativeType< T >> extends GetRandomAccessibleInterval< T >
{

	@RequiredFeature
	GetIterableInterval< T > ii;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Random Accessible Interval";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GetRandomAccessibleIntervalFromIterableInterval< T > copy()
	{
		return new GetRandomAccessibleIntervalFromIterableInterval< T >();
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings( "unchecked" )
	@Override
	protected RandomAccessibleInterval< T > recompute()
	{
		if ( ii instanceof RandomAccessibleInterval )
		{
			return ( RandomAccessibleInterval< T > ) ii;
		}
		else
		{
			return create( ii.get() );
		}

	}

	private Img< T > create( final IterableInterval< T > ii )
	{

		final Img< T > mask = new ArrayImgFactory< T >().create( ii, ii.firstElement().createVariable() );

		final RandomAccess< T > maskRA = mask.randomAccess();
		final Cursor< T > cur = ii.localizingCursor();

		while ( cur.hasNext() )
		{
			cur.fwd();
			for ( int d = 0; d < ii.numDimensions(); d++ )
			{
				maskRA.setPosition( cur.getLongPosition( d ) - ii.min( d ), d );
			}
			maskRA.get().set( cur.get() );
		}

		return mask;
	}
}
