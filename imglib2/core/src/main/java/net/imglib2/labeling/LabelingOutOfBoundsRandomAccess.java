package net.imglib2.labeling;

import java.util.ArrayList;

import net.imglib2.img.Img;
import net.imglib2.outofbounds.OutOfBoundsConstantValue;

public class LabelingOutOfBoundsRandomAccess< T extends Comparable< T >> extends OutOfBoundsConstantValue< LabelingType< T >>
{

	public < I extends Img< LabelingType< T >>> LabelingOutOfBoundsRandomAccess( final I labeling )
	{
		super( labeling, new LabelingType< T >( new ArrayList< T >() ) );
	}

}
