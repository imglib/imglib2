package net.imglib2.ops.descriptors.geometric.area;

import net.imglib2.IterableInterval;
import net.imglib2.ops.descriptors.ModuleInput;

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

	@Override
	public double priority()
	{
		return 1.0;
	}
}
