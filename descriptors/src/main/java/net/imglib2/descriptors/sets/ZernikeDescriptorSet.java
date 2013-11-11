package net.imglib2.descriptors.sets;

import net.imglib2.descriptors.AbstractDescriptorSet;
import net.imglib2.descriptors.moments.zernike.features.ZernikeComplexNumber;
import net.imglib2.descriptors.moments.zernike.features.ZernikeMagnitude;

public class ZernikeDescriptorSet extends AbstractDescriptorSet
{
	public ZernikeDescriptorSet()
	{
		super();

		registerFeature( ZernikeComplexNumber.class );
		registerFeature( ZernikeMagnitude.class );

	}

	@Override
	public String name()
	{
		return "Zernike Descriptor Set";
	}

}
