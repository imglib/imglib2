package net.imglib2.ops.features.providers.sources;

import net.imglib2.IterableInterval;
import net.imglib2.ops.features.AbstractSmartFeature;
import net.imglib2.ops.features.Feature;
import net.imglib2.ops.features.FeatureProcessorBuilder;

public class GetIterableInterval< T > extends AbstractSmartFeature< IterableInterval< T >>
{
	@SuppressWarnings( "unchecked" )
	@Override
	protected Feature< IterableInterval< T > > create( FeatureProcessorBuilder< ?, ? > processor )
	{
		Feature< ? > feature;
		if ( ( feature = processor.getFeature( GetIterableInterval.class ) ) != null ) { return ( Feature< IterableInterval< T >> ) feature; }
		throw new IllegalArgumentException( "I don't know how to create an source for an IterableInterval! Add it manually." );
	}
}
