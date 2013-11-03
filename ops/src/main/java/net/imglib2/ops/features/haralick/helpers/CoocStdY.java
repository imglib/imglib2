package net.imglib2.ops.features.haralick.helpers;

import net.imglib2.ops.features.annotations.RequiredInput;
import net.imglib2.ops.features.datastructures.CachedAbstractSampler;
import net.imglib2.type.numeric.real.DoubleType;

public class CoocStdY extends CachedAbstractSampler< DoubleType >
{
	// for symmetric cooccurence matrices stdx = stdy
	@RequiredInput
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
	protected DoubleType recompute()
	{
		return coocStdX.get();
	}

}
