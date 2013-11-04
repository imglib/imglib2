package net.imglib2.ops.descriptors.haralick.features;

import net.imglib2.ops.descriptors.AbstractFeature;
import net.imglib2.ops.descriptors.ModuleInput;
import net.imglib2.ops.descriptors.haralick.HaralickCoocMatrix;
import net.imglib2.ops.descriptors.haralick.helpers.CoocPXMinusY;
import net.imglib2.type.numeric.real.DoubleType;

public class DifferenceEntropy extends AbstractFeature
{

	// Avoid log 0
	private static final double EPSILON = 0.00000001f;

	@ModuleInput
	private HaralickCoocMatrix cooc;

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
	public DifferenceEntropy copy()
	{
		return new DifferenceEntropy();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DoubleType compute()
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
