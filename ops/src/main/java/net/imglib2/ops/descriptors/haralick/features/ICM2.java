package net.imglib2.ops.descriptors.haralick.features;

import net.imglib2.ops.descriptors.AbstractFeature;
import net.imglib2.ops.descriptors.ModuleInput;
import net.imglib2.ops.descriptors.haralick.helpers.CoocHXY;
import net.imglib2.type.numeric.real.DoubleType;

public class ICM2 extends AbstractFeature
{

	@ModuleInput
	Entropy entropy;

	@ModuleInput
	CoocHXY coocHXY;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "ICM2";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ICM2 copy()
	{
		return new ICM2();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DoubleType compute()
	{
		final double[] coochxy = coocHXY.get();
		final double res = Math.sqrt( 1 - Math.exp( -2 * ( coochxy[ 3 ] - entropy.get().get() ) ) );

		return new DoubleType( res );
	}

}
