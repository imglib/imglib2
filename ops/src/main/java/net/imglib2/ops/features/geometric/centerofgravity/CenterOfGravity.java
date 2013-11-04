package net.imglib2.ops.features.geometric.centerofgravity;

import net.imglib2.ops.features.AbstractDescriptorModule;
import net.imglib2.ops.features.Module;

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
