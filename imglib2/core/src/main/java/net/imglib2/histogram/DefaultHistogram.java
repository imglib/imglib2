package net.imglib2.histogram;

public class DefaultHistogram<T> {

	private final Iterable<T> iterable;
	private final BinMapper<T> binMapper;
	private final BinnedDistribution binDistrib;

	public DefaultHistogram(Iterable<T> iterable, BinMapper<T> binMapper)
	{
		this.iterable = iterable;
		this.binMapper = binMapper;
		this.binDistrib = new BinnedDistribution(binMapper.getBinDimensions());
		populateBins();
	}

	public long getBinPos(T value) {
		long[] pos = binMapper.getBinPosition(value);
		return pos[0];
	}

	public long numValues(T value) {
		long[] pos = binMapper.getBinPosition(value);
		return binDistrib.numValues(pos);
	}

	public double proportionOfValues(T value) {
		long[] pos = binMapper.getBinPosition(value);
		return binDistrib.proportionOfValues(pos);
	}

	public void recalc() {
		populateBins();
	}

	private void populateBins() {
		binDistrib.resetCounters();
		for (T value : iterable) {
			long[] binPos = binMapper.getBinPosition(value);
			// System.out.println("bin pos = " + binPos[0]);
			binDistrib.increment(binPos);
		}
	}
}
