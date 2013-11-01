package net.imglib2.ops.features.haralick.helpers;

import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.annotations.RequiredFeature;

public class CoocPY extends AbstractFeature< double[] >
{

	// in the symmetric case px = py
	@RequiredFeature
	CoocPX coocPX = new CoocPX();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Helper CoocPY";
	}

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
