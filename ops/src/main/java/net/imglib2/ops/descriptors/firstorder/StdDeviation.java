package net.imglib2.ops.descriptors.firstorder;

import net.imglib2.ops.descriptors.AbstractFeatureModule;
import net.imglib2.ops.descriptors.ModuleInput;

public class StdDeviation extends AbstractFeatureModule
{

	@ModuleInput
	private Variance variance;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Standard Deviation";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double calculateFeature()
	{
		return Math.sqrt( variance.value() );
	}

}
