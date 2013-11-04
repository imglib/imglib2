package net.imglib2.ops.features.firstorder;

import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.ModuleInput;
import net.imglib2.type.numeric.real.DoubleType;

public class StdDeviation extends AbstractFeature
{

	@ModuleInput
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
	public DoubleType compute()
	{
		return new DoubleType( Math.sqrt( variance.get().get() ) );
	}

}
