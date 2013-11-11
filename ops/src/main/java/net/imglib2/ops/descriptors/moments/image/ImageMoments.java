package net.imglib2.ops.descriptors.moments.image;

import net.imglib2.ops.descriptors.AbstractDescriptorModule;

public class ImageMoments extends AbstractDescriptorModule
{
	@Override
	public String name() 
	{
		return "Image moments up to order";
	}

	@Override
	protected double[] recompute() 
	{
		return null;
	}
}
