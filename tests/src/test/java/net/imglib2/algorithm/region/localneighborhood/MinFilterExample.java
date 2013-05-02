package net.imglib2.algorithm.region.localneighborhood;

import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;

public class MinFilterExample
{

	public static < T extends Type< T > & Comparable< T > >
	void minFilter( final RandomAccessibleInterval< T > input, final RandomAccessibleInterval< T > output, final Shape shape )
	{
		final RandomAccess< T > out = output.randomAccess();
		for ( final Neighborhood< T > neighborhood : shape.neighborhoods( input ) )
		{
			out.setPosition( neighborhood );
			final T o = out.get();
			o.set( neighborhood.firstElement() );
			for ( final T i : neighborhood )
				if ( i.compareTo( o ) < 0 )
					o.set( i );
		}
	}

	public static void main( final String[] args ) throws ImgIOException
	{
		final String fn = "/home/tobias/workspace/data/DrosophilaWing.tif";
		final int span = 3;

		final ArrayImgFactory< FloatType > factory = new ArrayImgFactory< FloatType >();
		final FloatType type = new FloatType();
		final Img< FloatType > imgInput = new ImgOpener().openImg( fn, factory, type );
		final Img< FloatType > imgOutput = factory.create( imgInput, type );

		final Interval computationInterval = Intervals.expand( imgInput, -span );
		final RandomAccessibleInterval< FloatType > input = Views.interval( imgInput, computationInterval );
		final RandomAccessibleInterval< FloatType > output = Views.interval( imgOutput, computationInterval );

		minFilter( input, output, new RectangleShape( span, false ) );
//		minFilter( input, output, new HyperSphereShape( span ) );

		ImageJFunctions.show( imgInput, "input" );
		ImageJFunctions.show( imgOutput, "min filtered" );
	}

}
