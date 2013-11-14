package net.imglib2.descriptors.geometric.diameter;

import net.imglib2.descriptors.AbstractFeatureModule;
import net.imglib2.descriptors.Module;

/**
 * 
 * Feret's Diameter - The longest distance between any two points along the selection boundary, 
 * also known as maximum caliper. (From ImageJ)
 * 
 * @author graumanna
 *
 */
public abstract class FeretsDiameter extends AbstractFeatureModule
{
	@Override
	public String name() 
	{
		return "Feret's Diameter";
	}
	
	@Override
	public boolean isEquivalentModule( Module< ? > output )
	{
		return FeretsDiameter.class.isAssignableFrom( output.getClass() );
	}
	
}
