package net.imglib2.ops.measure;



public class SampleStdDev implements Measurement {

	private SampleVariance sampVar;
	
	public SampleStdDev(SampleVariance variance) {
		this.sampVar = variance;
	}
	
	@Override
	public double getValue() {
		return Math.sqrt(sampVar.getValue());
	}
	
}

