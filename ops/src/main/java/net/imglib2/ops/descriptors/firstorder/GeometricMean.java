package net.imglib2.ops.descriptors.firstorder;

import net.imglib2.ops.descriptors.AbstractFeatureModule;
import net.imglib2.ops.descriptors.ModuleInput;
import net.imglib2.ops.descriptors.firstorder.sums.SumOfLogs;
import net.imglib2.ops.descriptors.geometric.area.Area;

public class GeometricMean extends AbstractFeatureModule
{
	@ModuleInput
	private SumOfLogs logSum;

	@ModuleInput
	private Area area;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Geometric Mean";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double calculateFeature()
	{
		double logSum = this.logSum.value();
		double area = this.area.value();

		return Math.exp( logSum / area );
	}
}
