package net.imglib2.descriptors.sets;

import net.imglib2.descriptors.AbstractDescriptorSet;
import net.imglib2.descriptors.geometric.Circularity;
import net.imglib2.descriptors.geometric.area.Area;
import net.imglib2.descriptors.geometric.centerofgravity.CenterOfGravity;
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

	}

	@Override
	public String name()
	{
		return "Geometric Features";
	}
}
