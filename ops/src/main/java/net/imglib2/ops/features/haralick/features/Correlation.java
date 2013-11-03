package net.imglib2.ops.features.haralick.features;

import net.imglib2.ops.data.CooccurrenceMatrix;
import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.RequiredInput;
import net.imglib2.ops.features.haralick.HaralickCoocMatrix;
import net.imglib2.ops.features.haralick.helpers.CoocMeanX;
import net.imglib2.ops.features.haralick.helpers.CoocMeanY;
import net.imglib2.ops.features.haralick.helpers.CoocStdX;
import net.imglib2.ops.features.haralick.helpers.CoocStdY;
import net.imglib2.type.numeric.real.DoubleType;

public class Correlation extends AbstractFeature
{

	@RequiredInput
	HaralickCoocMatrix cooc;

	@RequiredInput
	CoocMeanX coocMeanX = new CoocMeanX();

	@RequiredInput
	CoocMeanY coocMeanY = new CoocMeanY();

	@RequiredInput
	CoocStdX coocStdX = new CoocStdX();

	@RequiredInput
	CoocStdY coocStdY = new CoocStdY();

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
	protected DoubleType recompute()
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
