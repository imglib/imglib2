package net.imglib2.ops.features.geometric.centerofgravity;

import net.imglib2.Localizable;
import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.PositionIterator;
import net.imglib2.ops.features.annotations.RequiredFeature;
import net.imglib2.ops.features.geometric.Area;
import net.imglib2.ops.features.providers.sources.GetAreaIterator;

public class CenterOfGravityGeneric extends AbstractFeature< double[] >
{
	@RequiredFeature
	GetAreaIterator provider;

	@RequiredFeature
	Area area;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Center of Gravity";
	}

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
	protected double[] recompute()
	{
		PositionIterator it = provider.get();
		final double[] r = new double[ it.numDimensions() ];

		while ( it.hasNext() )
		{
			Localizable next = it.next();
			for ( int i = 0; i < r.length; i++ )
			{
				r[ i ] += next.getDoublePosition( i );
			}
		}

		for ( int i = 0; i < r.length; i++ )
		{
			r[ i ] /= area.get().getRealDouble();
		}

		return r;
	}
}
