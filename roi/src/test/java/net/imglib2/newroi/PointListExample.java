package net.imglib2.newroi;

import net.imglib2.Cursor;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.collection.PointSampleList;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.logic.BooleanConstant;
import net.imglib2.type.numeric.real.FloatType;

public class PointListExample
{
	public static void main( final String[] args )
	{
		final Img< FloatType > img = ArrayImgs.floats( 400, 300 );
		final RandomAccess< FloatType > output = img.randomAccess();

		final PointSampleList< BooleanConstant > pointsRoi = new PointSampleList< BooleanConstant >( 2 );
		pointsRoi.add( new Point( 150l, 100l ), BooleanConstant.True );
		pointsRoi.add( new Point( 250l, 100l ), BooleanConstant.True );
		pointsRoi.add( new Point( 150l, 200l ), BooleanConstant.True );
		pointsRoi.add( new Point( 250l, 200l ), BooleanConstant.True );

		final Cursor< BooleanConstant > c = pointsRoi.localizingCursor();
		while ( c.hasNext() )
		{
			c.fwd();
			output.setPosition( c );
			output.get().set( 1 );
		}

		ImageJFunctions.show( img );

//		for ( final FloatType t : Regions.sample( pointsRoi, img ) )
//		t.set( 1 );
	}

}
