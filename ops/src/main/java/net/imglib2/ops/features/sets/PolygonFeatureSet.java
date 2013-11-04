package net.imglib2.ops.features.sets;

import net.imglib2.ops.features.AbstractDescriptorSet;
import net.imglib2.ops.features.geometric.CenterOfGravityForDim;
import net.imglib2.ops.features.geometric.Circularity;
import net.imglib2.ops.features.geometric.area.Area2DPolygon;
import net.imglib2.ops.features.geometric.eccentricity.Eccentricity2DPolygon;
import net.imglib2.ops.features.geometric.perimeter.Perimeter2DPolygon;

public class PolygonFeatureSet extends AbstractDescriptorSet
{
	public PolygonFeatureSet( int numDims )
	{
		registerFeature( new Area2DPolygon() );
		registerFeature( new Perimeter2DPolygon() );
		registerFeature( new Eccentricity2DPolygon() );
		registerFeature( new Circularity() );

		for ( int i = 0; i < numDims; i++ )
		{
			registerFeature( new CenterOfGravityForDim( i ) );
		}
	}

	@Override
	public String name()
	{
		return "Geometric Features (Polygon based)";
	}
}
