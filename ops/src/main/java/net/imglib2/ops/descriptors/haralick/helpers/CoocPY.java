package net.imglib2.ops.descriptors.haralick.helpers;

import net.imglib2.ops.descriptors.AbstractModule;
import net.imglib2.ops.descriptors.ModuleInput;

public class CoocPY extends AbstractModule< double[] >
{

	// in the symmetric case px = py
	@ModuleInput
	CoocPX coocPX = new CoocPX();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CoocPY copy()
	{
		return new CoocPY();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected double[] recompute()
	{
		return coocPX.get();
	}

}
