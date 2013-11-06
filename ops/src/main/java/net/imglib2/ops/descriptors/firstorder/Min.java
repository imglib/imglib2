package net.imglib2.ops.descriptors.firstorder;

import java.util.Iterator;

import net.imglib2.ops.descriptors.AbstractFeatureModule;
import net.imglib2.ops.descriptors.ModuleInput;
import net.imglib2.ops.descriptors.firstorder.percentile.SortedValues;
import net.imglib2.type.numeric.RealType;

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
		return sortedValues.get()[0];
	}
}
