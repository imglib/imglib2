package net.imglib2.ops.features.providers;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.ops.features.annotations.RequiredFeature;
import net.imglib2.ops.features.providers.sources.GetBinaryMask;
import net.imglib2.ops.features.providers.sources.GetIterableInterval;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;

public class GetBinaryMaskFromIterableInterval extends GetBinaryMask
{

	@RequiredFeature
	GetIterableInterval< ? extends RealType< ? > > ii;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "BinaryMaskProvider";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GetBinaryMask copy()
	{
		return new GetBinaryMaskFromIterableInterval();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected RandomAccessibleInterval< BitType > recompute()
	{
		return binaryMask( ii.get() );
	}

	private Img< BitType > binaryMask( final IterableInterval< ? extends RealType< ? > > ii )
	{

		final Img< BitType > mask = new ArrayImgFactory< BitType >().create( ii, new BitType() );
		final double minval = ii.firstElement().getMinValue();

		final RandomAccess< BitType > maskRA = mask.randomAccess();
		final Cursor< ? extends RealType< ? > > cur = ii.localizingCursor();
		while ( cur.hasNext() )
		{
			cur.fwd();
			for ( int d = 0; d < ii.numDimensions(); d++ )
			{
				maskRA.setPosition( cur.getLongPosition( d ) - ii.min( d ), d );
			}
			maskRA.get().set( cur.get().getRealDouble() > minval );
		}

		return mask;
	}
}
