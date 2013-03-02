package net.imglib2.histogram.rev2;

import java.util.List;

import net.imglib2.EuclideanSpace;

public interface BinMapperNd<T> extends EuclideanSpace {

	boolean hasTails();

	boolean hasTails(int dim);

	long getBinCount();

	long getBinCount(int dim);

	void map(List<T> values, long[] binPos);

	void getCenterValues(long[] binPos, List<T> values);

	void getLowerBounds(long[] binPos, List<T> values);

	void getUpperBounds(long[] binPos, List<T> values);

	boolean includesUpperBounds(long[] binPos);

	boolean includesLowerBounds(long[] binPos);
}
