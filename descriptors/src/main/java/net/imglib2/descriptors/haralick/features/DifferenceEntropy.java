package net.imglib2.descriptors.haralick.features;

import net.imglib2.descriptors.AbstractFeatureModule;
import net.imglib2.descriptors.ModuleInput;
import net.imglib2.descriptors.haralick.helpers.CoocPXMinusY;
import net.imglib2.descriptors.haralick.helpers.CoocParameter;

public class DifferenceEntropy extends AbstractFeatureModule
{

	// Avoid log 0
	private static final double EPSILON = 0.00000001f;

	@ModuleInput
	CoocParameter param;

	@ModuleInput
	CoocPXMinusY coocPXMinusY;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Difference Entropy";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected double calculateFeature()
	{
		final double[] pxminusy = coocPXMinusY.get();
		final int nrGrayLevels = param.nrGrayLevels;

		double res = 0;
		for ( int k = 0; k <= nrGrayLevels - 1; k++ )
		{
			res += pxminusy[ k ] * Math.log( pxminusy[ k ] + EPSILON );
		}

		res = -res;

		return res;
	}

}
