package net.imglib2.kdtree;

import net.imglib2.RandomAccessibleInterval;

import java.util.function.IntFunction;
import java.util.function.Supplier;

/**
 * Stores the KDTree values.
 * <p>
 * Values (of type {@code T}) are stored as either a 1D {@code
 * RandomAccessibleInterval<T>}, or a {@code List<T>}. Individual values can be
 * accessed by {@link #valuesSupplier()}{@code .get().apply(i)}. {@code
 * valueSupplier().get()} returns a reusable {@code IntFunction<T>}. Here {@code
 * T} maybe a proxy that is reused in subsequent {@code apply(i)}.
 * <p>
 * {@link #values()} returns all values as a 1D {@code
 * RandomAccessibleInterval<T>}. (If data is stored as {@code List<T>}, it is
 * wrapped into a {@code ListImg}.)
 *
 * @param <T>
 * 		the type of values stored in the tree.
 */
abstract public class KDTreeValues<T> {
	protected final T type;

	public KDTreeValues(T type) {
		this.type = type;
	}

	public T type() {
		return type;
	}

	/**
	 * @return number of values in the tree
	 */
	abstract public int size();

	/**
	 * Get the values as a 1D {@code RandomAccessibleInterval}, for serialization.
	 */
	abstract public RandomAccessibleInterval<T> values();

	/**
	 * Get a {@code Supplier} that return {@code IntFunction<T>} to provide
	 * values for a given node indices. If the returned {@code IntFunction<T>}
	 * is stateful ({@code T} maybe a proxy that is reused in subsequent {@code
	 * apply(i)}} every {@link Supplier#get()} creates a new instance of the
	 * {@code IntFunction<T>}.
	 */
	abstract public Supplier<IntFunction<T>> valuesSupplier();
}
