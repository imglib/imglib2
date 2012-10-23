package net.imglib2.ops.measure.measurements;

import net.imglib2.ops.measure.Measurement;



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

