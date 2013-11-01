package net.imglib2.ops.features.haralick.helpers;

import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.annotations.RequiredFeature;
import net.imglib2.type.numeric.real.DoubleType;

public class CoocStdY extends AbstractFeature< DoubleType >
{

	// for symmetric cooccurence matrices stdx = stdy
	@RequiredFeature
	private CoocStdX coocStdX = new CoocStdX();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Helper CoocStdY";
	}

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
