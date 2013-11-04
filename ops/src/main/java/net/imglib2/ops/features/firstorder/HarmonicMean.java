package net.imglib2.ops.features.firstorder;

import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.ModuleInput;
import net.imglib2.ops.features.firstorder.sums.SumOfInverses;
import net.imglib2.ops.features.geometric.area.Area;
import net.imglib2.type.numeric.real.DoubleType;

public class HarmonicMean extends AbstractFeature
{
	@ModuleInput
	private SumOfInverses inverseSum;

	@ModuleInput
	private Area area;

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
	public HarmonicMean copy()
	{
		return new HarmonicMean();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DoubleType compute()
	{
		return new DoubleType( area.get().get() / inverseSum.get().get() );
	}
}
