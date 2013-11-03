package net.imglib2.ops.features.firstorder;

import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.RequiredInput;
import net.imglib2.ops.features.firstorder.moments.Moment2AboutMean;
import net.imglib2.type.numeric.real.DoubleType;

public class Variance extends AbstractFeature
{
	@RequiredInput
	Moment2AboutMean moment2;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Variance";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Variance copy()
	{
		return new Variance();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DoubleType recompute()
	{
		return new DoubleType( moment2.get().get() );
	}

}
