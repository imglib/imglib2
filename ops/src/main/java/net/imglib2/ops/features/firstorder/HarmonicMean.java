package net.imglib2.ops.features.firstorder;

import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.RequiredFeature;
import net.imglib2.ops.features.firstorder.sums.SumOfInverses;
import net.imglib2.ops.features.geometric.area.AreaIterableInterval;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

public class HarmonicMean< T extends RealType< T >> extends AbstractFeature< DoubleType >
{

	@RequiredFeature
	private SumOfInverses< T > inverseSum;

	@RequiredFeature
	private AreaIterableInterval area;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Harmonic Mean";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HarmonicMean< T > copy()
	{
		return new HarmonicMean< T >();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DoubleType recompute()
	{
		return new DoubleType( area.get().get() / inverseSum.get().get() );
	}
}
