package net.imglib2.ops.descriptors.sets;

import net.imglib2.ops.descriptors.AbstractDescriptorSet;
import net.imglib2.ops.descriptors.zernike.Zernike;

public class ZernikeDescriptorSet extends AbstractDescriptorSet  
{
	public ZernikeDescriptorSet()
	{
		super();

		registerFeature(Zernike.class );
	}
	
	@Override
	public String name() {
		return "Zernike Descriptor Set";
	}

}
