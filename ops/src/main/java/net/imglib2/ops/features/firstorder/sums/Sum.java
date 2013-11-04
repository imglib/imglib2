package net.imglib2.ops.features.firstorder.sums;

import net.imglib2.ops.features.AbstractFeatureModule;
import net.imglib2.ops.features.Module;

public abstract class Sum extends AbstractFeatureModule
{
	@Override
	public boolean isEquivalentModule( Module< ? > output )
	{
		return Sum.class.isAssignableFrom( output.getClass() );
	}

	@Override
	public String name()
	{
		return "Sum";
	}
}
