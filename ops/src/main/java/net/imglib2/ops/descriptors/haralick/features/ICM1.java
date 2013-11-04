package net.imglib2.ops.descriptors.haralick.features;

import net.imglib2.ops.descriptors.AbstractFeatureModule;
import net.imglib2.ops.descriptors.ModuleInput;
import net.imglib2.ops.descriptors.haralick.helpers.CoocHXY;

public class ICM1 extends AbstractFeatureModule
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
		return "ICM1";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected double calculateFeature()
	{
		final double[] coochxy = coocHXY.get();
		final double res = ( entropy.value() - coochxy[ 2 ] ) / Math.max( coochxy[ 0 ], coochxy[ 1 ] );

		return res;
	}

}
