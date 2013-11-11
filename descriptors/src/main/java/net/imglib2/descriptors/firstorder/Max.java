package net.imglib2.descriptors.firstorder;

import net.imglib2.descriptors.AbstractFeatureModule;
import net.imglib2.descriptors.ModuleInput;
import net.imglib2.descriptors.firstorder.impl.SortedValues;

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
		return sortedValues.get()[ size - 1 ];
	}

	@Override
	public String name()
	{
		return "Maximum";
	}
}
