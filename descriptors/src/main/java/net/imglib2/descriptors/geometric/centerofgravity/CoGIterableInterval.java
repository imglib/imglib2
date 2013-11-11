package net.imglib2.descriptors.geometric.centerofgravity;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.descriptors.ModuleInput;
import net.imglib2.descriptors.geometric.area.Area;

public class CoGIterableInterval extends CenterOfGravity
{
	@ModuleInput
	IterableInterval< ? > ii;

	@ModuleInput
	Area area;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double[] recompute()
	{
		final Cursor< ? > it = ii.cursor();
		final double[] r = new double[ it.numDimensions() ];

		while ( it.hasNext() )
		{
			it.fwd();
			for ( int i = 0; i < r.length; i++ )
			{
				r[ i ] += it.getDoublePosition( i );
			}
		}

		for ( int i = 0; i < r.length; i++ )
		{
			r[ i ] /= area.value();
		}

		return r;
	}

	@Override
	public double priority()
	{
		return 1;
	}
}
