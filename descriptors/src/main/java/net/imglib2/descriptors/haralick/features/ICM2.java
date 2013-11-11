package net.imglib2.descriptors.haralick.features;

import net.imglib2.descriptors.AbstractFeatureModule;
import net.imglib2.descriptors.ModuleInput;
import net.imglib2.descriptors.haralick.helpers.CoocHXY;

public class ICM2 extends AbstractFeatureModule
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
	protected double calculateFeature()
	{
		final double[] coochxy = coocHXY.get();
		final double res = Math.sqrt( 1 - Math.exp( -2 * ( coochxy[ 3 ] - entropy.value() ) ) );

		return res;
	}

}
