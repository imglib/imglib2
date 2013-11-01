package net.imglib2.ops.features.providers;

import net.imglib2.IterableInterval;
import net.imglib2.ops.features.Source;
import net.imglib2.ops.features.providers.sources.GetIterableInterval;

public class SourceGetIterableInterval< T > extends GetIterableInterval< T > implements Source< IterableInterval< T >>
{

	private IterableInterval< T > m_source;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Source: IterableIntervalUpdater";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SourceGetIterableInterval< T > copy()
	{
		return new SourceGetIterableInterval< T >();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IterableInterval< T > recompute()
	{
		return m_source;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update( final IterableInterval< T > source )
	{
		m_source = source;
	}
}
