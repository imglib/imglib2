package net.imglib2.ops.features.haralick.helpers;

import net.imglib2.ops.features.CachedAbstractSampler;
import net.imglib2.ops.features.RequiredInput;
import net.imglib2.type.numeric.real.DoubleType;

public class CoocMeanY extends CachedAbstractSampler< DoubleType >
{

	// for symmetric cooccurence matrices stdx = stdy
	@RequiredInput
	private CoocMeanX coocMeanX = new CoocMeanX();

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
