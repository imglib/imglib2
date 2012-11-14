package net.imglib2.newroi;

import static net.imglib2.newroi.Regions.accessible;
import static net.imglib2.newroi.Regions.iterablePositionable;
import static net.imglib2.newroi.Regions.sample;
import static net.imglib2.newroi.Regions.samplePositionable;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.newroi.util.PositionableIterableInterval;
import net.imglib2.type.BooleanType;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.logic.BoolType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

public class ErodeExample
{
	public static < A extends BooleanType< A >, B extends BooleanType< B >, C extends BooleanType< C > > void
		erode( final RandomAccessible< A > input, final PositionableIterableInterval< B > kernel, final RandomAccessibleInterval< C > output )
	{
		final IterableInterval< A > inputIterable = Regions.iterable( Views.interval( input, output ) );

		final RandomAccess< C > out = output.randomAccess();
L:		for ( final PositionableIterableInterval< A > i : sample( inputIterable, accessible( samplePositionable( kernel, input ) ) ) )
		{
			for ( final A t : i )
				if ( ! t.get() )
					continue L;
			out.setPosition( i );
			out.get().set( true );
		}
	}

	public static void main( final String args[] ) throws ImgIOException
	{
		final Img< FloatType > floatImg = new ImgOpener().openImg( "/home/tobias/workspace/data/quadtree.tif", new ArrayImgFactory< FloatType >(), new FloatType() );
		final Img< BitType > bitImg = new ArrayImgFactory< BitType >().create( floatImg, new BitType() );
		final Cursor< BitType > c = bitImg.cursor();
		for ( final FloatType t : floatImg )
			c.next().set( t.get() > 10 );

		ImageJFunctions.show( bitImg );


		final Img< BitType > output = new ArrayImgFactory< BitType >().create( floatImg, new BitType() );
		final PositionableIterableInterval< BoolType > roi = iterablePositionable( new HyperSphereRegion( 2, 10 ) );
		erode( bitImg, roi, output );

		ImageJFunctions.show( output );
	}
}
