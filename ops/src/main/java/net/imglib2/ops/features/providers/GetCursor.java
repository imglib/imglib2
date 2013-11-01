package net.imglib2.ops.features.providers;

import net.imglib2.Cursor;
import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.annotations.RequiredFeature;
import net.imglib2.ops.features.providers.sources.GetIterableInterval;

public class GetCursor< T > extends AbstractFeature< Cursor< T >>
{
	@RequiredFeature
	GetIterableInterval< T > ii;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Cursor Provider";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GetCursor< T > copy()
	{
		return new GetCursor< T >();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Cursor< T > get()
	{
		return recompute();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Cursor< T > recompute()
	{
		return ii.get().cursor();
	}
}
