package net.imglib2.descriptors.firstorder;

import net.imglib2.descriptors.AbstractFeatureModule;
import net.imglib2.descriptors.ModuleInput;
import net.imglib2.descriptors.firstorder.sums.Sum;
import net.imglib2.descriptors.geometric.area.Area;

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
