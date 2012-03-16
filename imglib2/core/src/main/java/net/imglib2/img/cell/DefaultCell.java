package net.imglib2.img.cell;

import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;

public final class DefaultCell< A extends ArrayDataAccess< A > > extends AbstractCell< A >
{
	private final A data;

	public DefaultCell( final A creator, final int[] dimensions, final long[] min, final int entitiesPerPixel )
	{
		super( dimensions, min );
		this.data = creator.createArray( numPixels * entitiesPerPixel );
	}

	@Override
	public A getData()
	{
		return data;
	}
}
