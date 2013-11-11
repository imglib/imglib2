package net.imglib2.descriptors.firstorder.percentile;

import net.imglib2.descriptors.ModuleInput;
import net.imglib2.descriptors.firstorder.impl.SortedValues;

public class Percentile75 extends Percentile
{
	@ModuleInput
	SortedValues sortedValues;

	@Override
	public String name()
	{
		return "Percentile 75";
	}

	@Override
	protected double calculateFeature()
	{

		return this.calculatePercentile( 0.75, sortedValues.get() );
	}
}
