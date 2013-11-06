package net.imglib2.ops.descriptors.firstorder.percentile;

import java.util.ArrayList;
import java.util.List;

import net.imglib2.ops.descriptors.ModuleInput;
import net.imglib2.ops.descriptors.firstorder.impl.SortedValues;
import net.imglib2.ops.operation.iterable.unary.MedianOp;

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

		
		ArrayList< Double > statistics = new ArrayList< Double >();
		
		double[] val = sortedValues.get();
		
		for (int i = 0; i < val.length; i++)
		{
			statistics.add(val[i]);
		}
		
		//return select( statistics, 0, statistics.size() - 1, statistics.size() / 2 );
		return this.calculatePercentile(0.25, sortedValues.get());
		
	}
	
	
}
