package net.imglib2.descriptors.moments.central.features;

import net.imglib2.descriptors.AbstractDescriptorModule;
import net.imglib2.descriptors.ModuleInput;
import net.imglib2.descriptors.moments.central.NormalizedCentralMomentComputer;
import net.imglib2.descriptors.moments.central.helper.ImageMomentsParameter;

public class NormalizedCentralMoments extends AbstractDescriptorModule
{
	@ModuleInput
	NormalizedCentralMomentComputer moments;

	@ModuleInput
	ImageMomentsParameter param;

	@Override
	public String name()
	{
		return "Central moments up to order " + param.getOrder();
	}

	@Override
	protected double[] recompute()
	{
		return moments.get();
	}

}
