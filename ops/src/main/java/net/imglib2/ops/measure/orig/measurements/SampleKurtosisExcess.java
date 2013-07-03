package net.imglib2.ops.measure.orig.measurements;

import net.imglib2.ops.measure.orig.Measurement;



public class SampleKurtosisExcess implements Measurement {
	private SampleKurtosis sampKurt;
	
	public SampleKurtosisExcess(SampleKurtosis kurtosis) {
		this.sampKurt = kurtosis;
	}

	@Override
	public double getValue() {
		return sampKurt.getValue() - 3;
	}
}

