package net.imglib2.ops.features.geometric.centerofgravity;

import java.awt.Polygon;

import net.imglib2.Sampler;
import net.imglib2.ops.features.AbstractModule;
import net.imglib2.ops.features.ModuleInput;
import net.imglib2.ops.features.geometric.area.Area;

public class CenterOfGravity2DPolygon extends CenterOfGravity
{
	@ModuleInput
	Area area;

	@ModuleInput
	Polygon polygon;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double[] calculateDescriptor()
	{
		double[] result = new double[ 2 ];

		// Yang Mingqiang:
		// A Survey of Shape Feature Extraction Techniques
		// in Pattern Recognition Techniques, Technology and Applications, 2008
		for ( int i = 0; i < polygon.npoints; i++ )
		{
			double x = polygon.xpoints[ i ];
			double x1 = polygon.xpoints[ i + 1 ];
			double y = polygon.ypoints[ i ];
			double y1 = polygon.ypoints[ i + 1 ];

			result[ 0 ] += ( x + x1 ) * ( x * y1 - x1 * y );
			result[ 1 ] += ( y + y1 ) * ( x * y1 - x1 * y );
		}

		double x = polygon.xpoints[ polygon.npoints - 1 ];
		double x1 = polygon.xpoints[ 0 ];
		double y = polygon.ypoints[ polygon.npoints - 1 ];
		double y1 = polygon.ypoints[ 0 ];

		result[ 0 ] += ( x + x1 ) * ( x * y1 - x1 * y );
		result[ 1 ] += ( y + y1 ) * ( x * y1 - x1 * y );

		result[ 0 ] = ( 1 / ( 6 * area.get().get() ) ) * Math.abs( result[ 0 ] );
		result[ 1 ] = ( 1 / ( 6 * area.get().get() ) ) * Math.abs( result[ 1 ] );

		return result;
	}

	@Override
	public Sampler< double[] > copy()
	{
		return new CenterOfGravity2DPolygon();
	}
}
