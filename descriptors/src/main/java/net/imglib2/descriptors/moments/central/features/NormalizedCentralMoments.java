package net.imglib2.descriptors.moments.central.features;

import net.imglib2.descriptors.AbstractDescriptorModule;
import net.imglib2.descriptors.ModuleInput;
import net.imglib2.descriptors.moments.central.CentralMomentComputer;
import net.imglib2.descriptors.moments.helper.ImageMomentsParameter;

public class NormalizedCentralMoments extends AbstractDescriptorModule
{
	@ModuleInput
	CentralMomentComputer moments;

	@ModuleInput
	ImageMomentsParameter param;

	@Override
	public String name()
	{
		return "Normalized central moments up to order " + param.getOrder();
	}

	@Override
	protected double[] recompute()
	{
		return null;
	}

}
