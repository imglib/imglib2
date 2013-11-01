package net.imglib2.ops.features.haralick.features;

import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.RequiredFeature;
import net.imglib2.ops.features.haralick.helpers.CoocHXY;
import net.imglib2.type.numeric.real.DoubleType;

public class ICM1 extends AbstractFeature< DoubleType >
{

	@RequiredFeature
	Entropy entropy;

	@RequiredFeature
	CoocHXY coocHXY;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "ICM1";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ICM1 copy()
	{
		return new ICM1();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DoubleType recompute()
	{

		final double[] coochxy = coocHXY.get();
		final double res = ( entropy.get().get() - coochxy[ 2 ] ) / Math.max( coochxy[ 0 ], coochxy[ 1 ] );

		return new DoubleType( res );
	}

}
