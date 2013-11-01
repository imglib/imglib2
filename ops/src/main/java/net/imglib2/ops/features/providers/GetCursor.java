package net.imglib2.ops.features.providers;

import net.imglib2.Cursor;
import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.RequiredFeature;

public class GetCursor< T > extends AbstractFeature< Cursor< T >>
{

	@RequiredFeature
	private final GetIterableInterval< T > provider = new GetIterableInterval< T >();

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
		return provider.get().cursor();
	}
}
