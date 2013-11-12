package net.imglib2.descriptors.moments.image.helper;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.descriptors.AbstractFeatureModule;
import net.imglib2.descriptors.ModuleInput;
import net.imglib2.type.numeric.RealType;

public class Moment11 extends AbstractFeatureModule
{
	@ModuleInput
	IterableInterval< ? extends RealType< ? >> ii;

	protected double computeMoment( int p, int q )
	{
		double result = 0.0;

		final Cursor< ? extends RealType< ? > > it = ii.localizingCursor();
		while ( it.hasNext() )
		{
			it.fwd();
			final double x = it.getIntPosition( 0 );
			final double y = it.getIntPosition( 1 );

			result += it.get().getRealDouble() * Math.pow( x, p ) * Math.pow( y, q );
		}

		return result;
	}

	@Override
	public String name() {
		return "Moment p = 1 and q = 0";
	}

	@Override
	protected double calculateFeature() {
		int p = 1; int q = 0;
		return computeMoment(p, q);
	}
}
