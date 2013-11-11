package net.imglib2.descriptors.sets;

import net.imglib2.descriptors.AbstractDescriptorSet;
import net.imglib2.descriptors.tamura.features.Coarseness;
import net.imglib2.descriptors.tamura.features.Contrast;
import net.imglib2.descriptors.tamura.features.Directionality;

public class TamuraFeatureSet extends AbstractDescriptorSet
{
	public TamuraFeatureSet()
	{
		registerFeature( Coarseness.class );
		registerFeature( Contrast.class );
		registerFeature( Directionality.class );
	}

	@Override
	public String name()
	{
		return "Tamura Feature";
	}
}
