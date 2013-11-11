package net.imglib2.ops.descriptors.moments.central.features;

import net.imglib2.ops.descriptors.AbstractDescriptorModule;
import net.imglib2.ops.descriptors.ModuleInput;
import net.imglib2.ops.descriptors.moments.central.CentralMomentComputer;
import net.imglib2.ops.descriptors.moments.helper.ImageMomentsParameter;

public class CentralMoments extends AbstractDescriptorModule
{
	@ModuleInput
	CentralMomentComputer moments;
	
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
