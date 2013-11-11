package net.imglib2.descriptors.firstorder;

import net.imglib2.descriptors.AbstractFeatureModule;
import net.imglib2.descriptors.ModuleInput;
import net.imglib2.descriptors.firstorder.impl.SortedValues;

public class Min extends AbstractFeatureModule
{
	@ModuleInput
	SortedValues sortedValues;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Minimum";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double calculateFeature()
	{
		return sortedValues.get()[ 0 ];
	}
}
