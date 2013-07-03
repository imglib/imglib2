package net.imglib2.ops.measure.orig.measurements;

import net.imglib2.ops.measure.orig.SamplingMeasurement;



public class Sum5 implements SamplingMeasurement {
	private boolean calculated = false;
	private double sum;
	
	public Sum5() {}
	
	@Override
	public void preprocess(long[] origin) {
		sum = 0;
	}

	@Override
	public void dataValue(long[] pos, double value) {
		sum += value*value*value*value*value;
	}

	@Override
	public void postprocess() {
		calculated = true;
	}

	@Override
	public double getValue() {
		if (!calculated) return Double.NaN;
		return sum;
	}
}

