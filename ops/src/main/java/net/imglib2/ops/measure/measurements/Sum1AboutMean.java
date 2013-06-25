package net.imglib2.ops.measure.measurements;

import net.imglib2.ops.measure.SamplingMeasurement;



public class Sum1AboutMean implements SamplingMeasurement {
	private SampleMean mean;
	private double sumDevs;
	private double meanVal;
	private boolean calculated = false;

	public Sum1AboutMean(SampleMean mean) {
		this.mean = mean;
	}
	
	@Override
	public void preprocess(long[] origin) {
		sumDevs = 0;
		meanVal = mean.getValue();
	}
	
	@Override
	public void dataValue(long[] position, double value) {
		sumDevs += value - meanVal;
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

