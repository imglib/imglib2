package net.imglib2.ops.features;

public abstract class AbstractFeature< A > implements Feature< A >
{

	protected boolean dirty = true;

	private A m_res;

	protected abstract A recompute();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public A get()
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
	public void update()
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
}
