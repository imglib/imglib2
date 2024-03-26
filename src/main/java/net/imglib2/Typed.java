package net.imglib2;

public interface Typed< T >
{
	/**
	 * Get an instance of {@code T}.
	 * <p>
	 * It should not be assumed that the returned {@code T} instance is an
	 * independent copy. In particular, repeated calls to {@code getType()} may
	 * return the same instance.
	 *
	 * @return an instance of {@code T}
	 */
	T getType();
}
