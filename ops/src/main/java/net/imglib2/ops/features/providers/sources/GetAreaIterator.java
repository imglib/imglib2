package net.imglib2.ops.features.providers.sources;

import net.imglib2.ops.features.AbstractSmartFeature;
import net.imglib2.ops.features.Feature;
import net.imglib2.ops.features.FeatureProcessorBuilder;
import net.imglib2.ops.features.PositionIterator;
import net.imglib2.ops.features.providers.GetCursorBasedPositionIterator;

public class GetAreaIterator extends AbstractSmartFeature< PositionIterator >
{

	@Override
	protected Feature< PositionIterator > create( FeatureProcessorBuilder< ?, ? > processor )
	{
		if ( processor.getFeature( GetIterableInterval.class ) != null )
		{
			return new GetCursorBasedPositionIterator();
		}
		else
		{
			throw new IllegalArgumentException( "can't find a good way to create a poylgon" );
		}
	}
}
