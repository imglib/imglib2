package net.imglib2.type;

public final class Index
{
	private int i = 0;

	/**
	 * Get the index.
	 * <p>
	 * This is used by accessors (e.g., a {@code Cursor}) to position {@code
	 * NativeType}s in the container, and by {@code NativeType}s to determine
	 * the offset into the underlying primitive array, where the value of the
	 * type is stored.
	 */
	public int get()
	{
		return i;
	}

	/**
	 * Set the index to {@code index}.
	 * <p>
	 * This is used by accessors (e.g., a {@code Cursor}) to position {@code
	 * NativeType}s in the container.
	 */
	public void set( final int index)
	{
		i = index;
	}

	/**
	 * Increment the index.
	 * <p>
	 * This is used by accessors (e.g., a {@code Cursor}) to position {@code
	 * NativeType}s in the container.
	 */
	public void inc()
	{
		++i;
	}

	/**
	 * Increase the index by {@code decrement} steps.
	 * <p>
	 * This is used by accessors (e.g., a {@code Cursor}) to position {@code
	 * NativeType}s in the container.
	 */
	public void inc( final int increment )
	{
		i += increment;
	}

	/**
	 * Decrement the index.
	 * <p>
	 * This is used by accessors (e.g., a {@code Cursor}) to position {@code
	 * NativeType}s in the container.
	 */
	public void dec()
	{
		--i;
	}

	/**
	 * Decrease the index by {@code decrement} steps.
	 * <p>
	 * This is used by accessors (e.g., a {@code Cursor}) to position {@code
	 * NativeType}s in the container.
	 */
	public void dec( final int decrement )
	{
		i -= decrement;
	}
}
