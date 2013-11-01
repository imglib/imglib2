package net.imglib2.ops.features.geometric;

import java.util.Set;

import net.imglib2.IterableInterval;
import net.imglib2.ops.features.AbstractFeatureSet;
import net.imglib2.ops.features.geometric.generic.GenericEccentricity;
import net.imglib2.type.numeric.real.DoubleType;

public class GeometricFeatureSet extends AbstractFeatureSet< IterableInterval< ? >, DoubleType >
{
	public GeometricFeatureSet( Set< String > active )
	{
		super( active );

		register( new Circularity() );
		register( new Perimeter() );
		register( new Eccentricity() );
		register( new GenericEccentricity() );
		register( new Area() );
		register( new CenterOfGravityForDim( 0 ) );
		register( new CenterOfGravityForDim( 1 ) );
	}

	@Override
	public String name()
	{
		return "Geometric Features";
	}

}
