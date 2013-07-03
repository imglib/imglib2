package net.imglib2.ops.measure.orig.measurements;

import net.imglib2.ops.measure.orig.SamplingMeasurement;


public class Maximum implements SamplingMeasurement {

	private double max;
	private boolean calculated = false;
	
	public Maximum() {}
	
	@Override
	public void preprocess(long[] origin) {
		max = Double.NEGATIVE_INFINITY;
	}

	@Override
	public void dataValue(long[] position, double value) {
		max = Math.max(max, value);
	}

	@Override
	public void postprocess() {
		calculated = true;
	}

	@Override
	public double getValue() {
		if (!calculated) return Double.NaN;
		return max;
	}

}
