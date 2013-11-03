package net.imglib2.ops.features;

import java.awt.Polygon;

import net.imglib2.ops.features.datastructures.AbstractFeatureSet;
import net.imglib2.ops.features.geometric.CenterOfGravityForDim;
import net.imglib2.ops.features.geometric.Circularity;
import net.imglib2.ops.features.geometric.area.Area2DPolygon;
import net.imglib2.ops.features.geometric.centerofgravity.CenterOfGravity2DPolygon;
import net.imglib2.ops.features.geometric.eccentricity.Eccentricity2DPolygon;
import net.imglib2.ops.features.geometric.perimeter.Perimeter2DPolygon;
import net.imglib2.type.numeric.real.DoubleType;

public class PolygonFeatureSet< P extends Polygon > extends AbstractFeatureSet< P, DoubleType >
{
	public PolygonFeatureSet( int numDims )
	{

		registerFeature( new Area2DPolygon() );
		registerFeature( new Perimeter2DPolygon() );
		registerFeature( new Eccentricity2DPolygon() );
		registerRequired( new CenterOfGravity2DPolygon() );

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
