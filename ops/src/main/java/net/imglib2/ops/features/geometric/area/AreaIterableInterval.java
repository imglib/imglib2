package net.imglib2.ops.features.geometric.area;

import net.imglib2.IterableInterval;
import net.imglib2.ops.features.ModuleInput;

public class AreaIterableInterval extends Area
{
	@ModuleInput
	IterableInterval< ? > ii;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double calculateFeature()
	{
		return ii.size();
	}
}
