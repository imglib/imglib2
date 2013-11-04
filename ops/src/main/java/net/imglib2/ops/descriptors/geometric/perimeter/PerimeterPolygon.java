package net.imglib2.ops.descriptors.geometric.perimeter;

import java.awt.Polygon;

import net.imglib2.ops.descriptors.ModuleInput;

//TODO: Please verfiy this computation or even better: make it correct:-)
public class PerimeterPolygon extends Perimeter
{
	@ModuleInput
	Polygon polygon;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double calculateFeature()
	{
		final int numPoints = polygon.npoints;

		double perimeter = dist( polygon.xpoints[ numPoints - 1 ], polygon.ypoints[ numPoints - 1 ], polygon.xpoints[ 0 ], polygon.ypoints[ 0 ] );

		for ( int i = 0; i < numPoints - 1; i++ )
		{
			perimeter += dist( polygon.xpoints[ i ], polygon.ypoints[ i ], polygon.xpoints[ i + 1 ], polygon.ypoints[ i + 1 ] );
		}

		return perimeter;
	}

	private double dist( final int x1, final int y1, final int x2, final int y2 )
	{
		return Math.sqrt( ( x1 - x2 ) * ( x1 - x2 ) + ( y1 - y2 ) * ( y1 - y2 ) );
	}

	@Override
	public double priority()
	{
		return 1.0;
	}
}
