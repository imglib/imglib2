package net.imglib2.ops.descriptors.firstorder;

import java.util.Iterator;

import net.imglib2.ops.descriptors.AbstractFeatureModule;
import net.imglib2.ops.descriptors.ModuleInput;
import net.imglib2.ops.descriptors.firstorder.percentile.SortedValues;
import net.imglib2.type.numeric.RealType;

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
