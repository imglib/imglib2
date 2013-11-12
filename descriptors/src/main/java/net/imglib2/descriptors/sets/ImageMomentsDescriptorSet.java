package net.imglib2.descriptors.sets;

import net.imglib2.descriptors.AbstractDescriptorSet;
import net.imglib2.descriptors.moments.hu.features.HuMoments;
import net.imglib2.descriptors.moments.image.features.CentralMoments;
import net.imglib2.descriptors.moments.image.features.Moments;
import net.imglib2.descriptors.moments.image.features.NormalizedCentralMoments;

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

		registerFeature( Moments.class );
		registerFeature( CentralMoments.class );
		registerFeature( NormalizedCentralMoments.class );
		registerFeature( HuMoments.class );
	}
}
