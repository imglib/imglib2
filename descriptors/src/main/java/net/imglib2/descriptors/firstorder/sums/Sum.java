package net.imglib2.descriptors.firstorder.sums;

import net.imglib2.descriptors.AbstractFeatureModule;
import net.imglib2.descriptors.Module;

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
