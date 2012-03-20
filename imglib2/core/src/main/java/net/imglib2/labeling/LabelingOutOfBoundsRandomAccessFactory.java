package net.imglib2.labeling;

import net.imglib2.img.Img;
import net.imglib2.outofbounds.OutOfBounds;
import net.imglib2.outofbounds.OutOfBoundsFactory;

public class LabelingOutOfBoundsRandomAccessFactory< T extends Comparable< T >, F extends Img< LabelingType< T >>> implements OutOfBoundsFactory< LabelingType< T >, F >
{

	@Override
	public OutOfBounds< LabelingType< T >> create( final F f )
	{
		return new LabelingOutOfBoundsRandomAccess< T >( f );
	}

}
