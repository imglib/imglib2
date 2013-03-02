package net.imglib2.histogram.rev2;

import net.imglib2.EuclideanSpace;

public interface BinMapper1d<T> extends EuclideanSpace {

	boolean hasTails();

	long getBinCount();

	long map(T value);

	void getCenterValue(long binPos, T value);

	void getLowerBound(long binPos, T value);

	void getUpperBound(long binPos, T value);

	boolean includesUpperBound(long binPos);

	boolean includesLowerBound(long binPos);
}
