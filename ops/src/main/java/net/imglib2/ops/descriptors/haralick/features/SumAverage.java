package net.imglib2.ops.descriptors.haralick.features;

import net.imglib2.ops.descriptors.AbstractFeatureModule;
import net.imglib2.ops.descriptors.ModuleInput;
import net.imglib2.ops.descriptors.haralick.helpers.CoocPXPlusY;
import net.imglib2.ops.descriptors.haralick.helpers.CoocParameter;

public class SumAverage extends AbstractFeatureModule
{

	@ModuleInput
	CoocPXPlusY coocPXPlusY;

	@ModuleInput
	CoocParameter param;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Sum Average";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected double calculateFeature()
	{
		double[] pxplusy = coocPXPlusY.get();
		int numGrayLevels = param.nrGrayLevels;

		double res = 0;
		for ( int i = 2; i <= 2 * numGrayLevels; i++ )
		{
			res += i * pxplusy[ i ];
		}

		return res;
	}

}
