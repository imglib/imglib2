package net.imglib2.ops.features.geometric;

import net.imglib2.ops.features.AbstractSmartFeature;
import net.imglib2.ops.features.Feature;
import net.imglib2.ops.features.FeatureProcessorBuilder;
import net.imglib2.ops.features.geometric.area.Area2DPolygon;
import net.imglib2.ops.features.geometric.area.AreaIterableInterval;
import net.imglib2.ops.features.providers.sources.GetIterableInterval;
import net.imglib2.ops.features.providers.sources.GetPolygon;
import net.imglib2.type.numeric.real.DoubleType;

public class Area extends AbstractSmartFeature< DoubleType >
{
	public Feature< DoubleType > create( FeatureProcessorBuilder< ?, ? > processor )
	{
		if ( processor.getFeature( GetPolygon.class ) != null )
			return ( new Area2DPolygon() );
		if ( processor.getFeature( GetIterableInterval.class ) != null )
			return ( new AreaIterableInterval() );

		return null;
	}
}
