package net.imglib2.newroi;

import static net.imglib2.newroi.Regions.accessible;
import static net.imglib2.newroi.Regions.iterablePositionable;
import static net.imglib2.newroi.Regions.sample;
import static net.imglib2.newroi.Regions.samplePositionable;
import net.imglib2.IterableInterval;
import net.imglib2.Point;
import net.imglib2.collection.PointSampleList;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.newroi.util.PositionableIterableInterval;
import net.imglib2.type.logic.BoolType;
import net.imglib2.type.logic.BooleanConstant;
import net.imglib2.type.numeric.real.FloatType;

public class NestingExample
{
	public static void main( final String args[] )
	{
		final Img< FloatType > img = ArrayImgs.floats( 400, 300 );

		final PositionableIterableInterval< BoolType > roi = iterablePositionable( new HyperSphereRegion( 2, 20 ) );

		final PointSampleList< BooleanConstant > pointsRoi = new PointSampleList< BooleanConstant >( 2 );
		pointsRoi.add( new Point( 150l, 100l ), BooleanConstant.True );
		pointsRoi.add( new Point( 250l, 100l ), BooleanConstant.True );
		pointsRoi.add( new Point( 150l, 200l ), BooleanConstant.True );
		pointsRoi.add( new Point( 250l, 200l ), BooleanConstant.True );

		for ( final IterableInterval< FloatType > i : sample( pointsRoi, accessible( samplePositionable( roi, img ) ) ) )
			for ( final FloatType t : i )
				t.set( 1 );

		ImageJFunctions.show( img );
	}
}
