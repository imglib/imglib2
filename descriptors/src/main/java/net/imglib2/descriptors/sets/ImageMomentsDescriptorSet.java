package net.imglib2.descriptors.sets;

import net.imglib2.descriptors.AbstractDescriptorSet;
import net.imglib2.descriptors.moments.central.features.NormalizedCentralMoments;

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

		registerFeature( NormalizedCentralMoments.class );
	}

}
