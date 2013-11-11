package net.imglib2.descriptors.moments.zernike.features;

import net.imglib2.descriptors.AbstractDescriptorModule;
import net.imglib2.descriptors.ModuleInput;
import net.imglib2.descriptors.moments.zernike.ZernikeMomentComputer;
import net.imglib2.descriptors.moments.zernike.helper.ZernikeParameter;

public class ZernikeComplexNumber extends AbstractDescriptorModule
{
	@ModuleInput
	ZernikeMomentComputer zernike;

	@ModuleInput
	ZernikeParameter param;

	@Override
	public String name()
	{
		return "Complex representation of Zernike Moment up to order " + param.getOrder();
	}

	@Override
	protected double[] recompute()
	{
		return zernike.get();
	}
}
