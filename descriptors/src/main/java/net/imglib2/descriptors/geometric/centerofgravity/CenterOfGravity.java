package net.imglib2.descriptors.geometric.centerofgravity;

import net.imglib2.descriptors.AbstractDescriptorModule;
import net.imglib2.descriptors.Module;

public abstract class CenterOfGravity extends AbstractDescriptorModule
{

	@Override
	public boolean isEquivalentModule( Module< ? > output )
	{
		return CenterOfGravity.class.isAssignableFrom( output.getClass() );
	}

	@Override
	public String name()
	{
		return "Center of Gravity";
	}
}
