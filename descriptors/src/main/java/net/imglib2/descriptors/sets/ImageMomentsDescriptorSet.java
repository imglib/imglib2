package net.imglib2.descriptors.sets;

import net.imglib2.descriptors.AbstractDescriptorSet;
import net.imglib2.descriptors.moments.central.features.CentralMoments;

public class ImageMomentsDescriptorSet extends AbstractDescriptorSet
{
	@Override
	public String name()
	{
		return "Image Moments Descriptor Set";
	}

	public ImageMomentsDescriptorSet()
	{
		super();

		registerFeature( CentralMoments.class );
	}

}
