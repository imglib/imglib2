package net.imglib2.ops.features.geometric;

import net.imglib2.ops.features.AbstractSmartFeature;
import net.imglib2.ops.features.Feature;
import net.imglib2.ops.features.FeatureProcessorBuilder;
import net.imglib2.ops.features.geometric.centerofgravity.CenterOfGravity2DPolygon;
import net.imglib2.ops.features.geometric.centerofgravity.CenterOfGravityGeneric;
import net.imglib2.ops.features.providers.sources.GetIterableInterval;
import net.imglib2.ops.features.providers.sources.GetPolygon;

public class CenterOfGravity extends AbstractSmartFeature< double[] >
{
	public Feature< double[] > create( FeatureProcessorBuilder< ?, ? > processor )
	{
		if ( processor.getFeature( GetPolygon.class ) != null )
			return new CenterOfGravity2DPolygon();
		if ( processor.getFeature( GetIterableInterval.class ) != null )
			return new CenterOfGravityGeneric();
		return null;
	}
}
