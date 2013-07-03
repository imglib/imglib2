package net.imglib2.ops.measure.orig.measurements;

import net.imglib2.ops.measure.orig.Measurement;



public class SampleStdDev implements Measurement {

	private SampleVariance sampVar;
	
	public SampleStdDev(SampleVariance variance) {
		this.sampVar = variance;
	}
	
	@Override
	public double getValue() {
		return Math.sqrt(sampVar.getValue());
	}
	
}

