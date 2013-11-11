package net.imglib2.descriptors.firstorder;

import net.imglib2.descriptors.AbstractFeatureModule;
import net.imglib2.descriptors.ModuleInput;

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
