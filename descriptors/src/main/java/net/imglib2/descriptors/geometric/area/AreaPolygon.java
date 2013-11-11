package net.imglib2.descriptors.geometric.area;

import java.awt.Polygon;

import net.imglib2.descriptors.ModuleInput;

public class AreaPolygon extends Area
{
	@ModuleInput
	Polygon polygon;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double calculateFeature()
	{
		double sum1 = 0.0f;
		double sum2 = 0.0f;

		// Yang Mingqiang:
		// A Survey of Shape Feature Extraction Techniques
		// in Pattern Recognition Techniques, Technology and Applications, 2008
		for ( int i = 0; i < polygon.npoints - 1; i++ )
		{
			sum1 += polygon.xpoints[ i ] * polygon.ypoints[ i + 1 ];
			sum2 += polygon.ypoints[ i ] * polygon.xpoints[ i + 1 ];
		}

		sum1 += polygon.xpoints[ polygon.npoints - 1 ] * polygon.ypoints[ 0 ];
		sum2 += polygon.ypoints[ polygon.npoints - 1 ] * polygon.xpoints[ 0 ];

		return Math.abs( sum1 - sum2 ) / 2;
	}

	@Override
	public double priority()
	{
		return -1;
	}
}
