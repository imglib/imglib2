package net.imglib2.ops.measure.measurements;

import net.imglib2.ops.measure.Measurement;



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

