package net.imglib2.descriptors.haralick.helpers;

import net.imglib2.descriptors.AbstractDescriptorModule;
import net.imglib2.descriptors.ModuleInput;

public class CoocPY extends AbstractDescriptorModule
{

	// in the symmetric case px = py
	@ModuleInput
	CoocPX coocPX = new CoocPX();

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected double[] recompute()
	{
		return coocPX.get();
	}

	@Override
	public String name()
	{
		return "CoocPY";
	}

}
