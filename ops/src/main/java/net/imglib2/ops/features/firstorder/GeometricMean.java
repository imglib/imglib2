package net.imglib2.ops.features.firstorder;

import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.ModuleInput;
import net.imglib2.ops.features.firstorder.sums.SumOfLogs;
import net.imglib2.ops.features.geometric.area.Area;
import net.imglib2.type.numeric.real.DoubleType;

public class GeometricMean extends AbstractFeature
{
	@ModuleInput
	private SumOfLogs logSum;

	@ModuleInput
	private Area area;

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
	public GeometricMean copy()
	{
		return new GeometricMean();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DoubleType compute()
	{
		double logSum = this.logSum.get().get();
		double area = this.area.get().get();

		return new DoubleType( Math.exp( logSum / area ) );
	}
}
