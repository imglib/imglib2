package net.imglib2.newroi;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.logic.BoolType;
import net.imglib2.type.numeric.real.FloatType;

public class HyperSphereExample
{
	public static void main( final String[] args )
	{
		final Img< FloatType > img = ArrayImgs.floats( 400, 300 );
		final RandomAccess< FloatType > output = img.randomAccess();

		final HyperSphereRegion circle = new HyperSphereRegion( 2, 20 );
		circle.setPosition( new long[] { 200, 150 } );

		final IterableInterval< BoolType > roi = Regions.iterable( circle );
		final Cursor< BoolType > c = roi.localizingCursor();
		while ( c.hasNext() )
		{
			c.fwd();
			output.setPosition( c );
			output.get().set( 1 );
		}

		ImageJFunctions.show( img );

//		final PositionableIterableInterval< BoolType > roi = Regions.iterablePositionable( circle  );
//		roi.setPosition( new long[] { 200, 150 } );
	}
}
