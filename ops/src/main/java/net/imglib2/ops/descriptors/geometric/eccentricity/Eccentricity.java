package net.imglib2.ops.descriptors.geometric.eccentricity;

import net.imglib2.ops.descriptors.AbstractFeatureModule;
import net.imglib2.ops.descriptors.Module;

public abstract class Eccentricity extends AbstractFeatureModule
{

	@Override
	public boolean isEquivalentModule( Module< ? > output )
	{
		return Eccentricity.class.isAssignableFrom( output.getClass() );
	}

	@Override
	public String name()
	{
		return "Eccentricity";
	}
}
