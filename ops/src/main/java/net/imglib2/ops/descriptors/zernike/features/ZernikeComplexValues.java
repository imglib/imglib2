package net.imglib2.ops.descriptors.zernike.features;

import net.imglib2.ops.descriptors.AbstractDescriptorModule;
import net.imglib2.ops.descriptors.ModuleInput;
import net.imglib2.ops.descriptors.zernike.Zernike;

public class ZernikeComplexValues extends AbstractDescriptorModule
{
	@ModuleInput
	Zernike zernike;
	
	@Override
	public String name() 
	{
		return "Zernike moment complex values";
	}

	@Override
	protected double[] recompute() 
	{
		return zernike.get();
	}
}
