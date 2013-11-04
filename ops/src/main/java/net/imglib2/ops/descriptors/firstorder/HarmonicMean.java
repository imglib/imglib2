package net.imglib2.ops.descriptors.firstorder;

import net.imglib2.ops.descriptors.AbstractFeatureModule;
import net.imglib2.ops.descriptors.ModuleInput;
import net.imglib2.ops.descriptors.firstorder.sums.SumOfInverses;
import net.imglib2.ops.descriptors.geometric.area.Area;

public class HarmonicMean extends AbstractFeatureModule
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
	public double calculateFeature()
	{
		return area.value() / inverseSum.value();
	}
}
