package net.imglib2.ops.descriptors.firstorder;

import net.imglib2.ops.descriptors.AbstractFeatureModule;
import net.imglib2.ops.descriptors.ModuleInput;
import net.imglib2.ops.descriptors.firstorder.sums.Sum;
import net.imglib2.ops.descriptors.geometric.area.Area;

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
