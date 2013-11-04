package net.imglib2.ops.descriptors.geometric.area;

import net.imglib2.ops.descriptors.AbstractFeatureModule;
import net.imglib2.ops.descriptors.Module;

public abstract class Area extends AbstractFeatureModule
{
	@Override
	public String name()
	{
		return "Area";
	}

	@Override
	public boolean isEquivalentModule( Module< ? > output )
	{
		return Area.class.isAssignableFrom( output.getClass() );
	}
}
