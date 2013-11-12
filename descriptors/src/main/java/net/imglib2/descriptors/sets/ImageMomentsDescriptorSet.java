package net.imglib2.descriptors.sets;

import net.imglib2.descriptors.AbstractDescriptorSet;
import net.imglib2.descriptors.moments.central.features.NormalizedCentralMoments;
import net.imglib2.descriptors.moments.central.helper.NormalizedCentralMoment20;
import net.imglib2.descriptors.moments.hu.features.HuMoments;

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
		registerFeature( HuMoments.class );
	}

}
