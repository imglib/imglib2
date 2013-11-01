package net.imglib2.ops.features.providers;

import net.imglib2.IterableInterval;
import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.Source;

public class GetIterableInterval< T > extends AbstractFeature< IterableInterval< T >> implements Source< IterableInterval< T >>
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
	public GetIterableInterval< T > copy()
	{
		return new GetIterableInterval< T >();
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
