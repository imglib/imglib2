package net.imglib2.ops.measure.measurements;

import net.imglib2.ops.measure.SamplingMeasurement;



public class Moment1AboutZero implements SamplingMeasurement {
	private ElementCount numElems;
	private double sum;
	private boolean calculated = false;

	public Moment1AboutZero(ElementCount numElems) {
		this.numElems = numElems;
	}
	
	@Override
	public void preprocess(long[] origin) {
		sum = 0;
	}
	
	@Override
	public void dataValue(long[] position, double value) {
		sum += value;
	}
	
	@Override
	public void postprocess() {
		calculated = true;
	}
	
	@Override
	public double getValue() {
		if (!calculated) return Double.NaN;
		return sum / numElems.getValue();
	}
	
}

