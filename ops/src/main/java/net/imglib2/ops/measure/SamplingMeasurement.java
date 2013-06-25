package net.imglib2.ops.measure;



public interface SamplingMeasurement extends Measurement {
	void preprocess(long[] origin);
	void dataValue(long[] position, double value);
	void postprocess();
}

