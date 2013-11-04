package net.imglib2.ops.features.haralick.features;

import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.ModuleInput;
import net.imglib2.ops.features.haralick.helpers.CoocStdX;
import net.imglib2.type.numeric.real.DoubleType;

public class Variance extends AbstractFeature
{

	@ModuleInput
	private CoocStdX coocStdX;

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
	protected DoubleType compute()
	{
		return new DoubleType( coocStdX.get().get() * coocStdX.get().get() );
	}

}
