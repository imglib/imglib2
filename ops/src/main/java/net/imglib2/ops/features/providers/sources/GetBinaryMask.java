package net.imglib2.ops.features.providers.sources;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.ops.features.AbstractSmartFeature;
import net.imglib2.ops.features.Feature;
import net.imglib2.ops.features.FeatureProcessorBuilder;
import net.imglib2.ops.features.providers.GetBinaryMaskFromIterableInterval;
import net.imglib2.type.logic.BitType;

public class GetBinaryMask extends AbstractSmartFeature< RandomAccessibleInterval< BitType >>
{
	// This will just be called if and only if this is added from a
	// dependency and there is NO source is available!
	@Override
	protected Feature< RandomAccessibleInterval< BitType > > create( FeatureProcessorBuilder< ?, ? > processor )
	{
		if (  processor.getFeature( GetIterableInterval.class ) != null )
		{
			return new GetBinaryMaskFromIterableInterval();
		}
		else
		{
			throw new IllegalArgumentException( "can't find a good way to create a poylgon" );
		}
	}
}
