package net.imglib2.ops.measure.measurements;

import net.imglib2.ops.measure.SamplingMeasurement;



public class Sum1 implements SamplingMeasurement {
	private boolean calculated = false;
	private double sum;
	
	public Sum1() {}
	
	@Override
	public void preprocess(long[] origin) {
		sum = 0;
	}

	@Override
	public void dataValue(long[] pos, double value) {
		sum += value;
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

