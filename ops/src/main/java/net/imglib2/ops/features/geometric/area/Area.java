package net.imglib2.ops.features.geometric.area;

import net.imglib2.ops.features.AbstractFeatureModule;
import net.imglib2.ops.features.Module;
import net.imglib2.ops.features.Descriptor;

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
