package net.imglib2.descriptors.geometric.perimeter;

import net.imglib2.descriptors.AbstractFeatureModule;
import net.imglib2.descriptors.Module;

public abstract class Perimeter extends AbstractFeatureModule
{

	@Override
	public boolean isEquivalentModule( Module< ? > output )
	{
		return Perimeter.class.isAssignableFrom( output.getClass() );
	}

	@Override
	public String name()
	{
		return "Perimeter";
	}
	// marker
}
