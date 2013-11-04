package net.imglib2.ops.features.sets;

import net.imglib2.ops.features.AbstractDescriptorSet;
import net.imglib2.ops.features.geometric.CenterOfGravityForDim;
import net.imglib2.ops.features.geometric.Circularity;
import net.imglib2.ops.features.geometric.area.AreaIterableInterval;
import net.imglib2.ops.features.geometric.eccentricity.GenericEccentricity;

public class GeometricFeatureSet extends AbstractDescriptorSet
{
	public GeometricFeatureSet( int numDims )
	{

		registerFeature( new AreaIterableInterval() );
		registerFeature( new GenericEccentricity() );
		registerFeature( new Circularity() );

		for ( int i = 0; i < numDims; i++ )
		{
			registerFeature( new CenterOfGravityForDim( i ) );
		}

	}

	@Override
	public String name()
	{
		return "Geometric Features (IterableInterval based)";
	}
}
