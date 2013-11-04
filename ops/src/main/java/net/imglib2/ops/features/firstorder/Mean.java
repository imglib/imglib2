package net.imglib2.ops.features.firstorder;

import net.imglib2.ops.features.AbstractFeatureModule;
import net.imglib2.ops.features.ModuleInput;
import net.imglib2.ops.features.firstorder.sums.Sum;
import net.imglib2.ops.features.geometric.area.Area;

public class Mean extends AbstractFeatureModule
{
	@ModuleInput
	Sum sum;

	@ModuleInput
	Area area;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Mean";
	}

	@Override
	public double calculateFeature()
	{
		return sum.value() / area.value();
	}
}
