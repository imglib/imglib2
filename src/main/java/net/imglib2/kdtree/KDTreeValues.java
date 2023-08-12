package net.imglib2.kdtree;

import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.list.ListImg;
import net.imglib2.util.Util;

import java.util.List;
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

	/**
	 * Stores KDTree values as 1D {@code RandomAccessibleInterval<T>}.
	 *
	 * @param <T>
	 * 		the type of values stored in the tree.
	 */
	public static class ImgValues<T> extends KDTreeValues<T> {

		private final RandomAccessibleInterval<T> values;
		private final Supplier<IntFunction<T>> valuesSupplier;

		public ImgValues(RandomAccessibleInterval<T> values) {
			super(Util.getTypeFromInterval(values));
			this.values = values;
			valuesSupplier = () -> {
				final RandomAccess<T> ra = values.randomAccess();
				return ra::setPositionAndGet;
			};
		}

		@Override
		public int size() {
			return (int) values.dimension(0);
		}

		@Override
		public RandomAccessibleInterval<T> values() {
			return values;
		}

		@Override
		public Supplier<IntFunction<T>> valuesSupplier() {
			return valuesSupplier;
		}
	}

	/**
	 * Stores KDTree values as List.
	 *
	 * @param <T>
	 * 		the type of values stored in the tree.
	 */
	public static class ListValues<T> extends KDTreeValues<T> {

		private final List<T> values;
		private final Supplier<IntFunction<T>> valuesSupplier;

		public ListValues(List<T> values) {
			super(KDTreeUtils.getType(values));
			this.values = values;
			valuesSupplier = () -> values::get;
		}

		@Override
		public int size() {
			return values.size();
		}

		@Override
		public RandomAccessibleInterval<T> values() {
			return ListImg.wrap(values, values.size());
		}

		@Override
		public Supplier<IntFunction<T>> valuesSupplier() {
			return valuesSupplier;
		}
	}

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

	public static <S> KDTreeValues<S> create(final RandomAccessibleInterval<S> values) {
		return new KDTreeValues.ImgValues<>(values);
	}

	public static <S> KDTreeValues<S> create(final List<S> values) {
		return new KDTreeValues.ListValues<>(values);
	}
}
