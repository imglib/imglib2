package net.imglib2.ops.features.haralick.features;

import net.imglib2.ops.features.annotations.RequiredInput;
import net.imglib2.ops.features.datastructures.AbstractFeature;
import net.imglib2.ops.features.haralick.HaralickCoocMatrix;
import net.imglib2.ops.features.haralick.helpers.CoocPXMinusY;
import net.imglib2.type.numeric.real.DoubleType;

public class DifferenceEntropy extends AbstractFeature
{

	// Avoid log 0
	private static final double EPSILON = 0.00000001f;

	@RequiredInput
	private HaralickCoocMatrix cooc;

	@RequiredInput
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
	public DifferenceEntropy copy()
	{
		return new DifferenceEntropy();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DoubleType recompute()
	{
		final double[] pxminusy = coocPXMinusY.get();
		final int nrGrayLevels = cooc.getNrGrayLevels();

		double res = 0;
		for ( int k = 0; k <= nrGrayLevels - 1; k++ )
		{
			res += pxminusy[ k ] * Math.log( pxminusy[ k ] + EPSILON );
		}

		res = -res;

		return new DoubleType( res );
	}

}
