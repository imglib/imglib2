package net.imglib2.descriptors.geometric.eccentricity;

import net.imglib2.descriptors.AbstractFeatureModule;
import net.imglib2.descriptors.Module;

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
