package net.imglib2.ops.descriptors.firstorder.percentile;

import net.imglib2.ops.descriptors.ModuleInput;

public class Percentile25 extends Percentile 
{
	@ModuleInput
	SortedValues sortedValues;

	@Override
	public String name() {
		return "Percentile 25";
	}
	
	@Override
	protected double calculateFeature() {
		
		return this.calculatePercentile(0.25, sortedValues.get());
	}
}
