package net.imglib2.ops.features.firstorder;

import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.RequiredFeature;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

public class StdDeviation< T extends RealType< T >> extends AbstractFeature< DoubleType >
{

	@RequiredFeature
	private Variance< T > variance;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Standard Deviation";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StdDeviation< T > copy()
	{
		return new StdDeviation< T >();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DoubleType recompute()
	{
		return new DoubleType( Math.sqrt( variance.get().get() ) );
	}

}
