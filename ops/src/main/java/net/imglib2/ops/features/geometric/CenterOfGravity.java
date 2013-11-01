package net.imglib2.ops.features.geometric;

import net.imglib2.Cursor;
import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.RequiredFeature;
import net.imglib2.ops.features.providers.GetLocalizingCursor;

public class CenterOfGravity extends AbstractFeature< double[] >
{

	@RequiredFeature
	GetLocalizingCursor< ? > provider;

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
	public CenterOfGravity copy()
	{
		return new CenterOfGravity();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected double[] recompute()
	{
		Cursor< ? > c = provider.get();
		final double[] r = new double[ c.numDimensions() ];

		while ( c.hasNext() )
		{
			c.fwd();
			for ( int i = 0; i < r.length; i++ )
			{
				r[ i ] += c.getDoublePosition( i );
			}
		}

		for ( int i = 0; i < r.length; i++ )
		{
			r[ i ] /= area.get().getRealDouble();
		}

		return r;
	}
}
