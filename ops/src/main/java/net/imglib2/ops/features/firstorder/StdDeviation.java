package net.imglib2.ops.features.firstorder;

import net.imglib2.ops.features.annotations.RequiredInput;
import net.imglib2.ops.features.datastructures.AbstractFeature;
import net.imglib2.type.numeric.real.DoubleType;

public class StdDeviation extends AbstractFeature
{

	@RequiredInput
	private Variance variance;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Standard Deviation";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StdDeviation copy()
	{
		return new StdDeviation();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DoubleType recompute()
	{
		return new DoubleType( Math.sqrt( variance.get().get() ) );
	}

}
