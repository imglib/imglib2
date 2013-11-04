package net.imglib2.ops.descriptors.haralick.helpers;

import net.imglib2.ops.descriptors.AbstractModule;
import net.imglib2.ops.descriptors.ModuleInput;
import net.imglib2.type.numeric.real.DoubleType;

public class CoocMeanY extends AbstractModule< DoubleType >
{
	// for symmetric cooccurence matrices stdx = stdy
	@ModuleInput
	private CoocMeanX coocMeanX;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CoocMeanY copy()
	{
		return new CoocMeanY();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DoubleType recompute()
	{
		return coocMeanX.get();
	}

}
