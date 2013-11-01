package net.imglib2.ops.features.providers;

import net.imglib2.Cursor;
import net.imglib2.Localizable;
import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.Feature;
import net.imglib2.ops.features.PositionIterator;
import net.imglib2.ops.features.RequiredFeature;

public class GetCursorBasedPositionIterator extends AbstractFeature< PositionIterator > implements GetPositionIterator
{

	@RequiredFeature
	GetLocalizingCursor< ? > cursor;

	@Override
	public String name()
	{
		return "Cursor Based Position Iterator";
	}

	@Override
	public Feature< PositionIterator > copy()
	{
		return new GetCursorBasedPositionIterator();
	}

	@Override
	protected PositionIterator recompute()
	{

		return new PositionIterator()
		{
			private Cursor< ? > it;

			{
				it = GetCursorBasedPositionIterator.this.cursor.get();
			}

			@Override
			public void remove()
			{
				it.remove();
			}

			@Override
			public Localizable next()
			{
				it.fwd();
				return it;
			}

			@Override
			public boolean hasNext()
			{
				return it.hasNext();
			}
		};
	}
}
