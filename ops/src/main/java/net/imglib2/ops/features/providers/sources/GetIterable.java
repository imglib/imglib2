package net.imglib2.ops.features.providers.sources;

import net.imglib2.ops.features.AbstractSmartFeature;
import net.imglib2.ops.features.Feature;
import net.imglib2.ops.features.FeatureProcessorBuilder;

public class GetIterable< T > extends AbstractSmartFeature< Iterable< T > >
{

	@SuppressWarnings( "unchecked" )
	@Override
	protected Feature< Iterable< T >> create( FeatureProcessorBuilder< ?, ? > processor )
	{
		if ( processor.getFeature( GetIterable.class ) != null ) { return ( Feature< Iterable< T >> ) processor.getFeature( GetIterable.class ); }
		return null;
	}
}
