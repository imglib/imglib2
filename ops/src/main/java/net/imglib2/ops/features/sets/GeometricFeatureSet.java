package net.imglib2.ops.features.sets;

import net.imglib2.IterableInterval;
import net.imglib2.ops.features.AbstractFeatureSet;
import net.imglib2.ops.features.geometric.CenterOfGravityForDim;
import net.imglib2.ops.features.geometric.Circularity;
import net.imglib2.ops.features.geometric.area.AreaIterableInterval;
import net.imglib2.ops.features.geometric.centerofgravity.CenterOfGravityGeneric;
import net.imglib2.ops.features.geometric.eccentricity.GenericEccentricity;
import net.imglib2.type.numeric.real.DoubleType;

public class GeometricFeatureSet< I extends IterableInterval< ? > > extends AbstractFeatureSet< I, DoubleType >
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

		registerRequired( new CenterOfGravityGeneric() );
	}

	@Override
	public String name()
	{
		return "Geometric Features (IterableInterval based)";
	}
}
