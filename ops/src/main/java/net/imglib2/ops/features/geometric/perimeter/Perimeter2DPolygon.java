package net.imglib2.ops.features.geometric.perimeter;

import java.awt.Polygon;

import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.RequiredInput;
import net.imglib2.type.numeric.real.DoubleType;

//TODO: Please verfiy this computation or even better: make it correct:-)
public class Perimeter2DPolygon extends AbstractFeature implements Perimeter
{
	@RequiredInput
	Polygon polygon;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DoubleType recompute()
	{

		final int numPoints = polygon.npoints;

		double perimeter = dist( polygon.xpoints[ numPoints - 1 ], polygon.ypoints[ numPoints - 1 ], polygon.xpoints[ 0 ], polygon.ypoints[ 0 ] );

		for ( int i = 0; i < numPoints - 1; i++ )
		{
			perimeter += dist( polygon.xpoints[ i ], polygon.ypoints[ i ], polygon.xpoints[ i + 1 ], polygon.ypoints[ i + 1 ] );
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
	public Perimeter2DPolygon copy()
	{
		return new Perimeter2DPolygon();
	}

}
