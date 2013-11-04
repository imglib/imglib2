package net.imglib2.ops.descriptors.haralick.features;

import net.imglib2.ops.descriptors.AbstractFeatureModule;
import net.imglib2.ops.descriptors.ModuleInput;
import net.imglib2.ops.descriptors.haralick.helpers.CoocPXMinusY;
import net.imglib2.ops.descriptors.haralick.helpers.CoocParameter;

public class Contrast extends AbstractFeatureModule
{

	@ModuleInput
	CoocPXMinusY coocPXMinusZ;

	@ModuleInput
	CoocParameter param;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Contrast";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected double calculateFeature()
	{
		final int nrGrayLevels = param.nrGrayLevels;
		final double[] pxminusxy = coocPXMinusZ.get();

		double res = 0;
		for ( int k = 0; k <= nrGrayLevels - 1; k++ )
		{
			res += k * k * pxminusxy[ k ];
		}

		return res;
	}

}
