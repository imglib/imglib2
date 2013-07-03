package net.imglib2.ops.measure.orig.measurements;

import net.imglib2.ops.measure.orig.Measurement;



public class SampleKurtosis implements Measurement {

	private PopulationKurtosis popKurt;
	private ElementCount numElems;
	
	public SampleKurtosis(PopulationKurtosis popKurt, ElementCount numElems) {
		this.popKurt = popKurt;
		this.numElems = numElems;
	}

	@Override
	public double getValue() {
		double n = numElems.getValue();
		double value = popKurt.getValue();
		value *= (n+1) + 6;
		value *= (n-1) / ((n-2) * (n-3));
		return value;
	}
	
}

