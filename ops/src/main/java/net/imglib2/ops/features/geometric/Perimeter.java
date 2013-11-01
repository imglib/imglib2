package net.imglib2.ops.features.geometric;

import net.imglib2.ops.features.AbstractSmartFeature;
import net.imglib2.ops.features.Feature;
import net.imglib2.ops.features.FeatureProcessorBuilder;
import net.imglib2.ops.features.geometric.perimeter.Perimeter2DPolygon;
import net.imglib2.ops.features.providers.sources.GetPolygon;
import net.imglib2.type.numeric.real.DoubleType;

public class Perimeter extends AbstractSmartFeature< DoubleType >
{
	@Override
	protected Feature< DoubleType > create( FeatureProcessorBuilder< ?, ? > processor )
	{
		if ( processor.getFeature( GetPolygon.class ) != null )
			return ( new Perimeter2DPolygon() );

		throw new IllegalArgumentException( "Can't instantiate perimeter. No compatible source is present." );
	}
}
