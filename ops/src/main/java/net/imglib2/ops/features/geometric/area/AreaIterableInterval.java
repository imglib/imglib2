package net.imglib2.ops.features.geometric.area;

import net.imglib2.ops.features.annotations.RequiredFeature;
import net.imglib2.ops.features.geometric.Area;
import net.imglib2.ops.features.providers.sources.GetIterableInterval;
import net.imglib2.type.numeric.real.DoubleType;

public class AreaIterableInterval extends Area
{

	@RequiredFeature
	GetIterableInterval< ? > provider;

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
		return new DoubleType( provider.get().size() );
	}
}
