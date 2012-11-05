package net.imglib2.ops.sandbox.sampling;



public abstract class AbstractSamplingIterator implements SamplingIterator {

	protected final double[] curr;

	public AbstractSamplingIterator(int n) {
		curr = new double[n];
	}

	@Override
	public double[] get() {
		return curr;
	}

	@Override
	public double getDoublePosition(int d) {
		return curr[d];
	}

	@Override
	public float getFloatPosition(int d) {
		return (float) curr[d];
	}

	@Override
	public int getIntPosition(int d) {
		return (int) Math.round(getDoublePosition(d));
	}

	@Override
	public long getLongPosition(int d) {
		return Math.round(getDoublePosition(d));
	}

	@Override
	public void jumpFwd(long steps) {
		for (long i = 0; i < steps; i++)
			fwd();
	}

	@Override
	public void localize(double[] position) {
		for (int i = 0; i < position.length; i++)
			position[i] = getDoublePosition(i);
	}

	@Override
	public void localize(float[] position) {
		for (int i = 0; i < position.length; i++)
			position[i] = getFloatPosition(i);
	}

	@Override
	public void localize(int[] position) {
		for (int i = 0; i < position.length; i++)
			position[i] = getIntPosition(i);
	}

	@Override
	public void localize(long[] position) {
		for (int i = 0; i < position.length; i++)
			position[i] = getLongPosition(i);
	}

	@Override
	public double[] next() {
		fwd();
		return get();
	}

	@Override
	public int numDimensions() {
		return curr.length;
	}

	@Override
	public void remove() {
		// ignore
	}

}
