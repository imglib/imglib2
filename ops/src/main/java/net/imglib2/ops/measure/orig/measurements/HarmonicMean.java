package net.imglib2.ops.measure.orig.measurements;

import net.imglib2.ops.measure.orig.SamplingMeasurement;

public class HarmonicMean implements SamplingMeasurement {

	private ElementCount numElems;
	private double sum;
	private boolean calculated = false;

	public HarmonicMean(ElementCount numElems) {
		this.numElems = numElems;
	}
	
	@Override
	public void preprocess(long[] origin) {
		sum = 0;
	}

	@Override
	public void dataValue(long[] position, double value) {
		sum += 1 / value;
	}

	@Override
	public void postprocess() {
		calculated = true;
	}


	@Override
	public double getValue() {
		if (!calculated) return Double.NaN;
		return numElems.getValue() / sum; // looks weird but it is correct
	}

}
