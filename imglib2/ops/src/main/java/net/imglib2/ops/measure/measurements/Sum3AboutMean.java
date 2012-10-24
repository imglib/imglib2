package net.imglib2.ops.measure.measurements;

import net.imglib2.ops.measure.SamplingMeasurement;



public class Sum3AboutMean implements SamplingMeasurement {
	private Mean mean;
	private double sumDevs;
	private double meanVal;
	private boolean calculated = false;

	public Sum3AboutMean(Mean mean) {
		this.mean = mean;
	}
	
	@Override
	public void preprocess(long[] origin) {
		sumDevs = 0;
		meanVal = mean.getValue();
	}
	
	@Override
	public void dataValue(long[] position, double value) {
		double dev = value - meanVal;
		sumDevs += dev*dev*dev;
	}
	
	@Override
	public void postprocess() {
		calculated = true;
	}
	
	@Override
	public double getValue() {
		if (!calculated) return Double.NaN;
		return sumDevs;
	}
	
}

