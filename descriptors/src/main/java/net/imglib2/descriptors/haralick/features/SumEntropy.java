package net.imglib2.descriptors.haralick.features;

import net.imglib2.descriptors.AbstractFeatureModule;
import net.imglib2.descriptors.ModuleInput;
import net.imglib2.descriptors.haralick.helpers.CoocPXPlusY;
import net.imglib2.descriptors.haralick.helpers.CoocParameter;

public class SumEntropy extends AbstractFeatureModule
{

	private static final double EPSILON = 0.00000001f;

	@ModuleInput
	CoocParameter param;

	@ModuleInput
	CoocPXPlusY coocPXPlusY;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Sum Entropy";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected double calculateFeature()
	{
		final double[] pxplusy = coocPXPlusY.get();
		final int numGrayLevels = param.distance;

		double res = 0;

		for ( int i = 2; i <= 2 * numGrayLevels; i++ )
		{
			res += pxplusy[ i ] * Math.log( pxplusy[ i ] + EPSILON );
		}

		res = -res;

		return res;
	}
}
