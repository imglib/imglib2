package net.imglib2.ops.descriptors.haralick.features;

import net.imglib2.ops.descriptors.AbstractFeatureModule;
import net.imglib2.ops.descriptors.ModuleInput;
import net.imglib2.ops.descriptors.haralick.helpers.CoocPXPlusY;
import net.imglib2.ops.descriptors.haralick.helpers.CoocParameter;

public class SumVariance extends AbstractFeatureModule
{

	@ModuleInput
	private SumAverage sumAverage;

	@ModuleInput
	private CoocPXPlusY coocPXPlusY;

	@ModuleInput
	CoocParameter param;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Sum Variance";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected double calculateFeature()
	{
		final double[] pxplusy = coocPXPlusY.get();
		final int nrGrayLevels = param.nrGrayLevels;
		final double average = this.sumAverage.value();

		double res = 0;
		for ( int i = 2; i <= 2 * nrGrayLevels; i++ )
		{
			res += ( i - average ) * ( i - average ) * pxplusy[ i ];
		}

		return res;
	}

}
