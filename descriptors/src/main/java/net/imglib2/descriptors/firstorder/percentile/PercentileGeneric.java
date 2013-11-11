package net.imglib2.descriptors.firstorder.percentile;

import net.imglib2.descriptors.ModuleInput;
import net.imglib2.descriptors.firstorder.impl.SortedValues;
import net.imglib2.descriptors.firstorder.percentile.helper.PercentileParameter;

public class PercentileGeneric extends Percentile
{
	@ModuleInput
	SortedValues sortedValues;

	@ModuleInput
	PercentileParameter param;

	@Override
	public String name()
	{
		return "Percentile " + ( int ) ( param.getP() * 100 );
	}

	@Override
	protected double calculateFeature()
	{
		return this.calculatePercentile( param.getP(), sortedValues.get() );
	}
}
