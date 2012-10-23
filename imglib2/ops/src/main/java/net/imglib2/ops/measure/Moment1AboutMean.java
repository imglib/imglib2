package net.imglib2.ops.measure;



public class Moment1AboutMean implements SamplingMeasurement {
	private Mean mean;
	private ElementCount numElems;
	private double sumDevs;
	private double meanVal;
	private boolean calculated = false;

	public Moment1AboutMean(Mean mean, ElementCount numElems) {
		this.mean = mean;
		this.numElems = numElems;
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
		return sumDevs / numElems.getValue();
	}
	
}

