package net.imglib2.ops.descriptors.haralick.features;

import net.imglib2.ops.descriptors.AbstractFeature;
import net.imglib2.ops.descriptors.ModuleInput;
import net.imglib2.ops.descriptors.haralick.HaralickCoocMatrix;
import net.imglib2.ops.descriptors.haralick.helpers.CoocPXPlusY;
import net.imglib2.type.numeric.real.DoubleType;

public class SumAverage extends AbstractFeature
{

	@ModuleInput
	private CoocPXPlusY coocPXPlusY;

	@ModuleInput
	private HaralickCoocMatrix cooc;

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
	public SumAverage copy()
	{
		return new SumAverage();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DoubleType compute()
	{
		double[] pxplusy = coocPXPlusY.get();
		int numGrayLevels = cooc.getNrGrayLevels();

		double res = 0;
		for ( int i = 2; i <= 2 * numGrayLevels; i++ )
		{
			res += i * pxplusy[ i ];
		}

		return new DoubleType( res );
	}

}
