package net.imglib2.ops.descriptors.firstorder;

import net.imglib2.ops.descriptors.AbstractFeatureModule;
import net.imglib2.ops.descriptors.ModuleInput;
import net.imglib2.ops.descriptors.firstorder.moments.Moment2AboutMean;

public class Variance extends AbstractFeatureModule
{
	@ModuleInput
	Moment2AboutMean moment2;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Variance";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double calculateFeature()
	{
		return moment2.value();
	}
}
