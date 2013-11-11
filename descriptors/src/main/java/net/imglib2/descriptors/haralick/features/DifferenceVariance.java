package net.imglib2.descriptors.haralick.features;

import net.imglib2.descriptors.AbstractFeatureModule;
import net.imglib2.descriptors.ModuleInput;
import net.imglib2.descriptors.haralick.helpers.CoocPXMinusY;

public class DifferenceVariance extends AbstractFeatureModule
{

	@ModuleInput
	CoocPXMinusY coocPXMinusY;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Difference Variance";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected double calculateFeature()
	{
		final double[] pxminusy = coocPXMinusY.get();

		double sum = 0.0d;
		double res = 0.0d;
		for ( int k = 0; k < pxminusy.length; k++ )
		{
			sum += k * pxminusy[ k ];
		}
		for ( int k = 0; k < pxminusy.length; k++ )
		{
			res += ( k - sum ) * pxminusy[ k ];
		}

		return res;
	}
}
