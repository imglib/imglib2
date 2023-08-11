package net.imglib2.kdtree;

import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.list.ListImg;
import net.imglib2.util.Util;

import java.util.List;
import java.util.function.IntFunction;
import java.util.function.Supplier;

/**
 * Stores KDTree values as 1D {@code RandomAccessibleInterval<T>}.
 *
 * @param <T>
 * 		the type of values stored in the tree.
 */
public class KDTreeValuesImg<T> extends KDTreeValues<T> {

	private final RandomAccessibleInterval<T> values;
	private final Supplier<IntFunction<T>> valuesSupplier;

	public KDTreeValuesImg(RandomAccessibleInterval<T> values) {
		super(Util.getTypeFromInterval(values));
		this.values = values;
		valuesSupplier = () -> {
			final RandomAccess<T> ra = values.randomAccess();
			return ra::setPositionAndGet;
		};
	}

	/**
	 * @return number of values in the tree
	 */
	@Override
	public int size() {
		return (int) values.dimension(0);
	}

	/**
	 * Get the values as a 1D {@code RandomAccessibleInterval}, for serialization.
	 */
	@Override
	public RandomAccessibleInterval<T> values() {
		return values;
	}

	/**
	 * Get a {@code Supplier} that return {@code IntFunction<T>} to provide
	 * values for a given node indices. If the returned {@code IntFunction<T>}
	 * is stateful ({@code T} maybe a proxy that is reused in subsequent {@code
	 * apply(i)}} every {@link Supplier#get()} creates a new instance of the
	 * {@code IntFunction<T>}.
	 */
	@Override
	public Supplier<IntFunction<T>> valuesSupplier() {
		return valuesSupplier;
	}
}
