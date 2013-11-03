package net.imglib2.ops.features.geometric.area;

import net.imglib2.IterableInterval;
import net.imglib2.ops.features.annotations.RequiredInput;
import net.imglib2.ops.features.datastructures.AbstractFeature;
import net.imglib2.type.numeric.real.DoubleType;

public class AreaIterableInterval extends AbstractFeature implements Area
{
	@RequiredInput
	IterableInterval< ? > ii;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Area";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AreaIterableInterval copy()
	{
		return new AreaIterableInterval();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DoubleType recompute()
	{
		return new DoubleType( ii.size() );
	}
}
