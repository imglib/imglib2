package net.imglib2.ops.features.haralick.helpers;

import net.imglib2.ops.features.AbstractModule;
import net.imglib2.ops.features.ModuleInput;

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
	protected double[] calculateDescriptor()
	{
		return coocPX.get();
	}

}
