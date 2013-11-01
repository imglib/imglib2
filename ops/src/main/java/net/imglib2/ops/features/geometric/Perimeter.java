package net.imglib2.ops.features.geometric;

import java.awt.Polygon;

import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.RequiredFeature;
import net.imglib2.ops.features.providers.GetPolygon;
import net.imglib2.type.numeric.real.DoubleType;

//TODO: Please verfiy this computation or even better: make it correct:-)
public class Perimeter extends AbstractFeature< DoubleType >
{

	@RequiredFeature
	GetPolygon polygonGet;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DoubleType recompute()
	{

		final Polygon poly = polygonGet.get();
		final int numPoints = poly.npoints;

		double perimeter = dist( poly.xpoints[ numPoints - 1 ], poly.ypoints[ numPoints - 1 ], poly.xpoints[ 0 ], poly.ypoints[ 0 ] );

		for ( int i = 0; i < numPoints - 1; i++ )
		{
			perimeter += dist( poly.xpoints[ i ], poly.ypoints[ i ], poly.xpoints[ i + 1 ], poly.ypoints[ i + 1 ] );
		}

		return new DoubleType( perimeter );
	}

	private double dist( final int x1, final int y1, final int x2, final int y2 )
	{
		return Math.sqrt( ( x1 - x2 ) * ( x1 - x2 ) + ( y1 - y2 ) * ( y1 - y2 ) );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Perimeter Feature";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Perimeter copy()
	{
		return new Perimeter();
	}

}
