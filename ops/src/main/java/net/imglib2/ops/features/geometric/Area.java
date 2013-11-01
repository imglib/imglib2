package net.imglib2.ops.features.geometric;

import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.RequiredFeature;
import net.imglib2.ops.features.providers.GetIterableInterval;
import net.imglib2.type.numeric.real.DoubleType;

public class Area extends AbstractFeature< DoubleType >
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
	public Area copy()
	{
		return new Area();
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
