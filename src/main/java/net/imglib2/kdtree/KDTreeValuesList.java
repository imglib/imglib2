package net.imglib2.kdtree;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.list.ListImg;

import java.util.List;
import java.util.function.IntFunction;
import java.util.function.Supplier;

/**
 * Stores KDTree values as List.
 *
 * @param <T>
 * 		the type of values stored in the tree.
 */
public class KDTreeValuesList<T> extends KDTreeValues<T> {

	private final List<T> values;
	private final Supplier<IntFunction<T>> valuesSupplier;

	public KDTreeValuesList(List<T> values) {
		super(KDTreeUtils.getType(values));
		this.values = values;
		valuesSupplier = () -> values::get;
	}

	/**
	 * @return number of values in the tree
	 */
	public int size() {
		return values.size();
	}

	/**
	 * Get the values as a 1D {@code RandomAccessibleInterval}, for serialization.
	 */
	public RandomAccessibleInterval<T> values() {
		return ListImg.wrap(values, values.size());
	}

	/**
	 * Get a {@code Supplier} that return {@code IntFunction<T>} to provide
	 * values for a given node indices. If the returned {@code IntFunction<T>}
	 * is stateful ({@code T} maybe a proxy that is reused in subsequent {@code
	 * apply(i)}} every {@link Supplier#get()} creates a new instance of the
	 * {@code IntFunction<T>}.
	 */
	public Supplier<IntFunction<T>> valuesSupplier() {
		return valuesSupplier;
	}
}
