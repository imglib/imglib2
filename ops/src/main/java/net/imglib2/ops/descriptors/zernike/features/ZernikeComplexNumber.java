package net.imglib2.ops.descriptors.zernike.features;

import net.imglib2.ops.descriptors.AbstractDescriptorModule;
import net.imglib2.ops.descriptors.ModuleInput;
import net.imglib2.ops.descriptors.zernike.ZernikeMomentComputer;

public class ZernikeComplexNumber extends AbstractDescriptorModule
{
	@ModuleInput
	ZernikeMomentComputer zernike;
	
	@Override
	public String name() {
		return "Complex representation of Zernike Moment";
	}

	@Override
	protected double[] recompute() {
		return zernike.get();
	}
}
