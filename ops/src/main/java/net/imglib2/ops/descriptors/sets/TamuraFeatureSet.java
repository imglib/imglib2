package net.imglib2.ops.descriptors.sets;

import net.imglib2.ops.descriptors.AbstractDescriptorSet;
import net.imglib2.ops.descriptors.tamura.features.Coarseness;

public class TamuraFeatureSet extends AbstractDescriptorSet 
{

	public TamuraFeatureSet() 
	{
		registerFeature(Coarseness.class);
	}
	
	@Override
	public String name() 
	{
		return "Tamura Feature";
	}

}
