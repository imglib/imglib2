package net.imglib2.ops.descriptors.haralick.features;

import net.imglib2.ops.descriptors.AbstractFeature;
import net.imglib2.ops.descriptors.ModuleInput;
import net.imglib2.ops.descriptors.haralick.HaralickCoocMatrix;
import net.imglib2.ops.descriptors.haralick.helpers.CoocPXMinusY;
import net.imglib2.type.numeric.real.DoubleType;

public class Contrast extends AbstractFeature
{

	@ModuleInput
	CoocPXMinusY coocPXMinusZ = new CoocPXMinusY();

	@ModuleInput
	private HaralickCoocMatrix cooc;

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
	public Contrast copy()
	{
		return new Contrast();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DoubleType compute()
	{

		final int nrGrayLevels = cooc.getNrGrayLevels();
		final double[] pxminusxy = coocPXMinusZ.get();

		double res = 0;
		for ( int k = 0; k <= nrGrayLevels - 1; k++ )
		{
			res += k * k * pxminusxy[ k ];
		}

		return new DoubleType( res );
	}

}
