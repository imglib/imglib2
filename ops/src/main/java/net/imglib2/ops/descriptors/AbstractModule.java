package net.imglib2.ops.descriptors;

/**
 * Straightforward implementation of a {@link CachedModule}
 */
public abstract class AbstractModule< T > implements CachedModule< T >
{
	// cached result
	private T m_res;

	// if source updated the pipeline, dirty = true, feature should be
	// recomputed
	protected boolean dirty = true;

	// recompute the feature on get()
	protected abstract T recompute();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T get()
	{

		if ( dirty )
		{
			m_res = recompute();
			dirty = false;
		}

		return m_res;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void markDirty()
	{
		dirty = true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals( final Object obj )
	{
		return obj.hashCode() == hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode()
	{
		return getClass().getName().hashCode();
	}

	@Override
	public double priority()
	{
		return 0;
	}

	@Override
	public boolean isDirty()
	{
		return dirty;
	}
}
