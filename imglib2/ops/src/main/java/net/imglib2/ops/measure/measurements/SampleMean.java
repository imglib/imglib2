package net.imglib2.ops.measure.measurements;

import net.imglib2.ops.measure.Measurement;



public class SampleMean implements Measurement {
	private Moment1AboutZero moment1;

	public SampleMean(Moment1AboutZero moment1) {
		this.moment1 = moment1;
	}

	@Override
	public double getValue() {
		return moment1.getValue();
	}
	
}

