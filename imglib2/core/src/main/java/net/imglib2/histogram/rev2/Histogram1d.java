package net.imglib2.histogram.rev2;



public class Histogram1d<T> {

	private BinMapper1d<T> mapper;
	private Iterable<T> data;
	private DiscreteFrequencyDistribution distrib;
	private long[] pos;

	public Histogram1d(Iterable<T> data, BinMapper1d<T> mapper) {
		this.data = data;
		this.mapper = mapper;
		this.distrib =
			new DiscreteFrequencyDistribution(new long[] { mapper.getBinCount() });
		this.pos = new long[1];
		populateBins();
	}

	boolean hasTails() {
		return mapper.hasTails();
	}

	long lowerTailCount() {
		if (!hasTails()) return 0;
		pos[0] = 0;
		return distrib.frequency(pos);
	}

	long upperTailCount() {
		if (!hasTails()) return 0;
		pos[0] = mapper.getBinCount() - 1;
		return distrib.frequency(pos);
	}

	long valueCount() {
		return totalCount() - lowerTailCount() - upperTailCount();
	}

	long totalCount() {
		return distrib.totalValues();
	}

	long frequency(T value) {
		long bin = mapper.map(value);
		return frequency(bin);
	}

	long frequency(long binPos) {
		if (binPos < 0 || binPos >= mapper.getBinCount()) return 0;
		pos[0] = binPos;
		return distrib.frequency(pos);
	}

	double relativeFrequency(T value, boolean includeTails) {
		long bin = mapper.map(value);
		return relativeFrequency(bin, includeTails);
	}

	double relativeFrequency(long binPos, boolean includeTails) {
		double numer = frequency(binPos);
		long denom = includeTails ? totalCount() : valueCount();
		return numer / denom;
	}

	long getBinCount() {
		return mapper.getBinCount();
	}

	long getBinPosition(T value) {
		return mapper.map(value);
	}

	void recalc() {
		populateBins();
	}

	void getCenterValue(long binPos, T value) {
		mapper.getCenterValue(binPos, value);
	}

	void getLowerBound(long binPos, T value) {
		mapper.getLowerBound(binPos, value);
	}

	void getUpperBound(long binPos, T value) {
		mapper.getUpperBound(binPos, value);
	}

	boolean includesUpperBound(long binPos) {
		return mapper.includesUpperBound(binPos);
	}

	boolean includesLowerBound(long binPos) {
		return mapper.includesLowerBound(binPos);
	}

	// TODO - what about out of bounds bins : Long.min or Long.max?

	boolean isInLowerTail(T value) {
		if (!hasTails()) return false;
		long bin = mapper.map(value);
		return bin == 0;
	}

	boolean isInUpperTail(T value) {
		if (!hasTails()) return false;
		long bin = mapper.map(value);
		return bin == getBinCount() - 1;
	}

	boolean isInMiddle(T value) {
		if (!hasTails()) return true;
		long bin = mapper.map(value);
		return (bin > 0) && (bin < getBinCount() - 1);
	}

	// -- helpers --

	private void populateBins() {
		distrib.resetCounters();
		for (T value : data) {
			long bin = mapper.map(value);
			if (bin == Long.MIN_VALUE || bin == Long.MAX_VALUE) continue;
			pos[0] = bin;
			distrib.increment(pos);
		}
	}
}
