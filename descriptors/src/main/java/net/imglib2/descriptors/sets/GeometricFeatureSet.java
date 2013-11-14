package net.imglib2.descriptors.sets;

import net.imglib2.descriptors.AbstractDescriptorSet;
import net.imglib2.descriptors.geometric.Circularity;
import net.imglib2.descriptors.geometric.area.Area;
import net.imglib2.descriptors.geometric.centerofgravity.CenterOfGravity;
import net.imglib2.descriptors.geometric.diameter.FeretsDiameter;
import net.imglib2.descriptors.geometric.diameter.FeretsDiameterIterableInterval;
import net.imglib2.descriptors.geometric.diameter.FeretsDiameterPolygon;
import net.imglib2.descriptors.geometric.eccentricity.Eccentricity;
import net.imglib2.descriptors.geometric.perimeter.Perimeter;

public class GeometricFeatureSet extends AbstractDescriptorSet
{
	public GeometricFeatureSet()
	{

		registerFeature( Area.class );
		registerFeature( Eccentricity.class );
		registerFeature( Circularity.class );
		registerFeature( Perimeter.class );
		registerFeature( CenterOfGravity.class );
		registerFeature( FeretsDiameter.class );
	}

	@Override
	public String name()
	{
		return "Geometric Features";
	}
}
