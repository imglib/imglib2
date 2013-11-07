package net.imglib2.ops.descriptors.sets;

import net.imglib2.ops.descriptors.AbstractDescriptorSet;
import net.imglib2.ops.descriptors.zernike.Zernike;
import net.imglib2.ops.descriptors.zernike.features.ZernikeComplexMagnitude;
import net.imglib2.ops.descriptors.zernike.features.ZernikeComplexValues;

public class ZernikeDescriptorSet extends AbstractDescriptorSet  
{
	public ZernikeDescriptorSet()
	{
		super();

		registerFeature(ZernikeComplexValues.class );
		registerFeature(ZernikeComplexMagnitude.class );
	}
	
	@Override
	public String name() {
		return "Zernike Descriptor Set";
	}

}
