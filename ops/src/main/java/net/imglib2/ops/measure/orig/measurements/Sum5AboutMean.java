package net.imglib2.ops.measure.orig.measurements;

import net.imglib2.ops.measure.orig.SamplingMeasurement;



public class Sum5AboutMean implements SamplingMeasurement {
	private SampleMean mean;
	private double sumDevs;
	private double meanVal;
	private boolean calculated = false;

	public Sum5AboutMean(SampleMean mean) {
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
		sumDevs += dev*dev*dev*dev*dev;
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

