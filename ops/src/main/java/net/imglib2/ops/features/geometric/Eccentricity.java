package net.imglib2.ops.features.geometric;

import net.imglib2.ops.features.AbstractSmartFeature;
import net.imglib2.ops.features.Feature;
import net.imglib2.ops.features.FeatureProcessorBuilder;
import net.imglib2.ops.features.geometric.generic.Eccentricity2DPolygon;
import net.imglib2.ops.features.geometric.generic.GenericEccentricity;
import net.imglib2.ops.features.providers.sources.GetIterableInterval;
import net.imglib2.ops.features.providers.sources.GetPolygon;
import net.imglib2.type.numeric.real.DoubleType;

public class Eccentricity extends AbstractSmartFeature< DoubleType >
{
	@Override
	protected Feature< DoubleType > create( FeatureProcessorBuilder< ?, ? > processor )
	{
		if ( processor.getFeature( GetPolygon.class ) != null )
		{
			return new Eccentricity2DPolygon();
		}
		else if ( processor.getFeature( GetIterableInterval.class ) != null ) { return new GenericEccentricity(); }

		return null;
	}
}
