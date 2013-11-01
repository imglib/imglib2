package net.imglib2.ops.features.providers.sources;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.ops.features.AbstractSmartFeature;
import net.imglib2.ops.features.Feature;
import net.imglib2.ops.features.FeatureProcessorBuilder;
import net.imglib2.ops.features.providers.GetRandomAccessibleIntervalFromIterableInterval;
import net.imglib2.type.NativeType;

public class GetRandomAccessibleInterval< T extends NativeType< T > > extends AbstractSmartFeature< RandomAccessibleInterval< T > >
{

	@Override
	protected Feature< RandomAccessibleInterval< T >> create( FeatureProcessorBuilder< ?, ? > processor )
	{
		if ( processor.getFeature( GetIterableInterval.class ) != null )
		{
			return new GetRandomAccessibleIntervalFromIterableInterval< T >();
		}
		else
		{
			throw new IllegalArgumentException( "can't find a smart way to create a RandomAccessibleInterval from the given sources" );
		}
	}
}
