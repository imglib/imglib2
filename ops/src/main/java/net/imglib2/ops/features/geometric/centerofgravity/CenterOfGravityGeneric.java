package net.imglib2.ops.features.geometric.centerofgravity;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.ops.features.CachedAbstractSampler;
import net.imglib2.ops.features.RequiredInput;
import net.imglib2.ops.features.geometric.area.Area;

public class CenterOfGravityGeneric extends CachedAbstractSampler< double[] > implements CenterOfGravity
{
	@RequiredInput
	IterableInterval< ? > ii;

	@RequiredInput
	Area area;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CenterOfGravityGeneric copy()
	{
		return new CenterOfGravityGeneric();
	}

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
			r[ i ] /= area.get().get();
		}

		return r;
	}

	@Override
	public boolean isCompatible( Class< ? > c )
	{
		return CenterOfGravity.class.isAssignableFrom( c );
	}
}
