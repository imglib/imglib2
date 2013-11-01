package net.imglib2.ops.features.providers.sources;

import java.awt.Polygon;

import net.imglib2.ops.features.AbstractSmartFeature;
import net.imglib2.ops.features.Feature;
import net.imglib2.ops.features.FeatureProcessorBuilder;
import net.imglib2.ops.features.providers.GetPolygonFromBitmask;

public class GetPolygon extends AbstractSmartFeature< Polygon >
{

	@Override
	protected Feature< Polygon > create( FeatureProcessorBuilder< ?, ? > processor )
	{
		if ( processor.getFeature( GetIterableInterval.class ) != null )
		{
			return new GetPolygonFromBitmask();
		}
		else
		{
			throw new IllegalArgumentException( "can't find a good way to create a poylgon" );
		}
	}
}
