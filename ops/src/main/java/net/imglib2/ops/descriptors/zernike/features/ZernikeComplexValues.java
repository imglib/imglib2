package net.imglib2.ops.descriptors.zernike.features;

import net.imglib2.ops.descriptors.AbstractDescriptorModule;
import net.imglib2.ops.descriptors.ModuleInput;
import net.imglib2.ops.descriptors.zernike.Zernike;
import net.imglib2.ops.descriptors.zernike.ZernikeParameter;

public class ZernikeComplexValues extends AbstractDescriptorModule
{
	@ModuleInput
	ZernikeParameter param;
	
	@ModuleInput
	Zernike zernike;
	
	@Override
	public String name() 
	{
		return "Value of Zernike Moment of order: " + param.getN() + " and repetition: " + param.getM();
	}

	@Override
	protected double[] recompute() 
	{
		return zernike.get();
	}
}
