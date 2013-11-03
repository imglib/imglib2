package net.imglib2.ops.features.haralick.features;

import net.imglib2.ops.features.annotations.RequiredInput;
import net.imglib2.ops.features.datastructures.AbstractFeature;
import net.imglib2.ops.features.haralick.HaralickCoocMatrix;
import net.imglib2.ops.features.haralick.helpers.CoocPXPlusY;
import net.imglib2.type.numeric.real.DoubleType;

public class SumEntropy extends AbstractFeature
{

	private static final double EPSILON = 0.00000001f;

	@RequiredInput
	private HaralickCoocMatrix cooc;

	@RequiredInput
	private CoocPXPlusY coocPXPlusY;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Sum Entropy";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SumEntropy copy()
	{
		return new SumEntropy();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DoubleType recompute()
	{
		final double[] pxplusy = coocPXPlusY.get();
		final int numGrayLevels = cooc.getDistance();

		double res = 0;

		for ( int i = 2; i <= 2 * numGrayLevels; i++ )
		{
			res += pxplusy[ i ] * Math.log( pxplusy[ i ] + EPSILON );
		}

		res = -res;

		return new DoubleType( res );
	}
}
