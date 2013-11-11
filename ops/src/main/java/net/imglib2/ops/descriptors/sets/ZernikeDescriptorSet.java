package net.imglib2.ops.descriptors.sets;

import net.imglib2.ops.descriptors.AbstractDescriptorSet;
import net.imglib2.ops.descriptors.zernike.features.ZernikeComplexNumber;
import net.imglib2.ops.descriptors.zernike.features.ZernikeMagnitude;

public class ZernikeDescriptorSet extends AbstractDescriptorSet  
{
	public ZernikeDescriptorSet()
	{
		super();

		registerFeature(ZernikeComplexNumber.class );
		registerFeature(ZernikeMagnitude.class );

	}
	
	@Override
	public String name() {
		return "Zernike Descriptor Set";
	}

}
