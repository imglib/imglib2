package net.imglib2.ops.measure.measurements;

import net.imglib2.ops.measure.SamplingMeasurement;


public class Minimum implements SamplingMeasurement {

	private double min;
	private boolean calculated = false;
	
	public Minimum() {}
	
	@Override
	public void preprocess(long[] origin) {
		min = Double.POSITIVE_INFINITY;
	}

	@Override
	public void dataValue(long[] position, double value) {
		min = Math.min(min, value);
	}

	@Override
	public void postprocess() {
		calculated = true;
	}

	@Override
	public double getValue() {
		if (!calculated) return Double.NaN;
		return min;
	}

}
