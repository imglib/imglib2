package net.imglib2.ops.features.geometric;

import java.util.Set;

import net.imglib2.IterableInterval;
import net.imglib2.ops.features.AbstractFeatureSet;
import net.imglib2.ops.features.geometric.area.Area2DPolygon;
import net.imglib2.ops.features.geometric.area.AreaIterableInterval;
import net.imglib2.ops.features.geometric.generic.Eccentricity2DPolygon;
import net.imglib2.ops.features.geometric.generic.GenericEccentricity;
import net.imglib2.ops.features.geometric.perimeter.Perimeter2DPolygon;
import net.imglib2.ops.features.providers.GetIterableInterval;
import net.imglib2.type.numeric.real.DoubleType;

public class GeometricFeatureSet extends AbstractFeatureSet< IterableInterval< ? >, DoubleType >
{
	@SuppressWarnings( "rawtypes" )
	public GeometricFeatureSet( Set< String > active, int numDims )
	{
		super( new GetIterableInterval(), active );

		register( new AreaIterableInterval() );

		register( new Circularity() );
		if ( numDims == 2 )
		{
			register( new Perimeter2DPolygon() );
			register( new Eccentricity2DPolygon() );
		}
		else
		{
			register( new GenericEccentricity() );
			register( new Area2DPolygon() );
		}

		for ( int d = 0; d < numDims; d++ )
		{
			register( new AreaIterableInterval() );
			register( new CenterOfGravityForDim( 0 ) );
			register( new CenterOfGravityForDim( 1 ) );
		}
	}

	@Override
	public String name()
	{
		return "Geometric Features";
	}

}
