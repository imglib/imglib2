package net.imglib2.ops.descriptors.haralick.features;

import net.imglib2.ops.data.CooccurrenceMatrix;
import net.imglib2.ops.descriptors.AbstractFeature;
import net.imglib2.ops.descriptors.ModuleInput;
import net.imglib2.ops.descriptors.haralick.HaralickCoocMatrix;
import net.imglib2.ops.descriptors.haralick.helpers.CoocMeanX;
import net.imglib2.ops.descriptors.haralick.helpers.CoocMeanY;
import net.imglib2.ops.descriptors.haralick.helpers.CoocStdX;
import net.imglib2.ops.descriptors.haralick.helpers.CoocStdY;
import net.imglib2.type.numeric.real.DoubleType;

public class Correlation extends AbstractFeature
{
	@ModuleInput
	HaralickCoocMatrix cooc;

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
	public Correlation copy()
	{
		return new Correlation();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DoubleType compute()
	{
		final int nrGrayLevels = cooc.getNrGrayLevels();
		final CooccurrenceMatrix matrix = cooc.get();

		final double meanx = coocMeanX.get().get();
		final double meany = coocMeanY.get().get();
		final double stdx = coocStdX.get().get();
		final double stdy = coocStdY.get().get();

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

		return new DoubleType( res );
	}
}
