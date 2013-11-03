package net.imglib2.ops.features.geometric.area;

import java.awt.Polygon;

import net.imglib2.ops.features.annotations.RequiredInput;
import net.imglib2.ops.features.datastructures.AbstractFeature;
import net.imglib2.type.numeric.real.DoubleType;

public class Area2DPolygon extends AbstractFeature implements Area
{
	@RequiredInput
	Polygon polygon;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Area Polygon";
	}

	@Override
	public boolean isCompatible( Class< ? > clazz )
	{
		return Area.class.isAssignableFrom( clazz );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Area2DPolygon copy()
	{
		return new Area2DPolygon();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DoubleType recompute()
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

		double result = Math.abs( sum1 - sum2 ) / 2;

		return new DoubleType( result );
	}
}
