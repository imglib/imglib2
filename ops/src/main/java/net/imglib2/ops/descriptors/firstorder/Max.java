package net.imglib2.ops.descriptors.firstorder;

import net.imglib2.ops.descriptors.AbstractFeatureModule;
import net.imglib2.ops.descriptors.ModuleInput;
import net.imglib2.ops.descriptors.firstorder.impl.SortedValues;

public class Max extends AbstractFeatureModule
{
	@ModuleInput
	SortedValues sortedValues;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double calculateFeature()
	{
		int size = sortedValues.get().length;
		return sortedValues.get()[size-1];
	}

	@Override
	public String name()
	{
		return "Maximum";
	}
}
