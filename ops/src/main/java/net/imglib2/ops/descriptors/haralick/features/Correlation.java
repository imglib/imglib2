package net.imglib2.ops.descriptors.haralick.features;

import net.imglib2.ops.data.CooccurrenceMatrix;
import net.imglib2.ops.descriptors.AbstractFeatureModule;
import net.imglib2.ops.descriptors.ModuleInput;
import net.imglib2.ops.descriptors.haralick.CoocccurrenceMatrix;
import net.imglib2.ops.descriptors.haralick.helpers.CoocMeanX;
import net.imglib2.ops.descriptors.haralick.helpers.CoocMeanY;
import net.imglib2.ops.descriptors.haralick.helpers.CoocParameter;
import net.imglib2.ops.descriptors.haralick.helpers.CoocStdX;
import net.imglib2.ops.descriptors.haralick.helpers.CoocStdY;

public class Correlation extends AbstractFeatureModule
{
	@ModuleInput
	CoocParameter param;

	@ModuleInput
	CoocccurrenceMatrix cooc;

	@ModuleInput
	CoocMeanX coocMeanX;

	@ModuleInput
	CoocMeanY coocMeanY;

	@ModuleInput
	CoocStdX coocStdX;

	@ModuleInput
	CoocStdY coocStdY;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Correlation";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected double calculateFeature()
	{
		final int nrGrayLevels = param.nrGrayLevels;
		final CooccurrenceMatrix matrix = cooc.get();

		final double meanx = coocMeanX.value();
		final double meany = coocMeanY.value();
		final double stdx = coocStdX.value();
		final double stdy = coocStdY.value();

		double res = 0;
		for ( int i = 0; i < nrGrayLevels; i++ )
		{
			for ( int j = 0; j < nrGrayLevels; j++ )
			{
				res += ( ( i - meanx ) * ( j - meany ) ) * matrix.getValueAt( i, j ) / ( stdx * stdy );
			}
		}

		// if NaN
		if ( Double.isNaN( res ) )
		{
			res = 0;
		}

		return res;
	}
}
