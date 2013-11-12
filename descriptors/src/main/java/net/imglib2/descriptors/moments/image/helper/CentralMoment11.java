package net.imglib2.descriptors.moments.image.helper;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.descriptors.AbstractFeatureModule;
import net.imglib2.descriptors.ModuleInput;
import net.imglib2.descriptors.geometric.centerofgravity.CenterOfGravity;
import net.imglib2.type.numeric.RealType;

public class CentralMoment11 extends AbstractFeatureModule
{
	@ModuleInput
	IterableInterval< ? extends RealType< ? >> ii;

	@ModuleInput
	CenterOfGravity center;

	protected double computeCentralMoment( int p, int q )
	{
		final double centerX = center.get()[ 0 ];
		final double centerY = center.get()[ 1 ];

		double result = 0.0;

		final Cursor< ? extends RealType< ? > > it = ii.localizingCursor();
		while ( it.hasNext() )
		{
			it.fwd();
			final double x = it.getIntPosition( 0 ) - centerX;
			final double y = it.getIntPosition( 1 ) - centerY;

			result += it.get().getRealDouble() * Math.pow( x, p ) * Math.pow( y, q );
		}

		return result;
	}

	@Override
	public String name() {
		return "Central moment p = 1 and q = 1";
	}

	@Override
	protected double calculateFeature() {
		int p = 1; int q = 1;
		return computeCentralMoment(p, q);
	}
}
