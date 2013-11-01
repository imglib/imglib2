package net.imglib2.ops.features.providers;

import net.imglib2.Cursor;
import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.annotations.RequiredFeature;
import net.imglib2.ops.features.providers.sources.GetIterableInterval;

public class GetLocalizingCursor< T > extends AbstractFeature< Cursor< T >>
{
	@RequiredFeature
	GetIterableInterval< T > ii;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Localizing Cursor Provider";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GetLocalizingCursor< T > copy()
	{
		return new GetLocalizingCursor< T >();
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
		return ii.get().localizingCursor();
	}
}
