package net.imglib2.ops.measure.measurements;

import net.imglib2.ops.measure.SamplingMeasurement;



public class Product implements SamplingMeasurement {
	private boolean calculated = false;
	private double prod;
	
	public Product() {}
	
	@Override
	public void preprocess(long[] origin) {
		prod = 1;
	}

	@Override
	public void dataValue(long[] position, double value) {
		prod *= value;
	}

	@Override
	public void postprocess() {
		calculated = true;
	}

	@Override
	public double getValue() {
		if (!calculated) return Double.NaN;
		return prod;
	}
}

