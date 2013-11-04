package net.imglib2.ops.features.haralick.helpers;

import net.imglib2.ops.features.AbstractModule;
import net.imglib2.ops.features.ModuleInput;
import net.imglib2.type.numeric.real.DoubleType;

public class CoocStdY extends AbstractModule< DoubleType >
{
	// for symmetric cooccurence matrices stdx = stdy
	@ModuleInput
	private CoocStdX coocStdX = new CoocStdX();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CoocStdY copy()
	{
		return new CoocStdY();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DoubleType calculateDescriptor()
	{
		return coocStdX.get();
	}

}
