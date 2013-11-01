package net.imglib2.ops.features.firstorder;

import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.annotations.RequiredFeature;
import net.imglib2.ops.features.firstorder.sums.SumOfLogs;
import net.imglib2.ops.features.geometric.area.AreaIterableInterval;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

public class GeometricMean< T extends RealType< T >> extends AbstractFeature< DoubleType >
{

	@RequiredFeature
	private SumOfLogs< T > logSum;

	@RequiredFeature
	private AreaIterableInterval area;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Geometric Mean";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GeometricMean< T > copy()
	{
		return new GeometricMean< T >();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DoubleType recompute()
	{
		double logSum = this.logSum.get().get();
		double area = this.area.get().get();

		return new DoubleType( Math.exp( logSum / area ) );
	}
}
