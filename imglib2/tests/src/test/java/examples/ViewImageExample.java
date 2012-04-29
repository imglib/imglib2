package examples;

import gui.Interactive2DViewer;

import java.util.ArrayList;

import net.imglib2.FinalInterval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.display.RealARGBConverter;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.realtransform.AffineTransform2D;
import net.imglib2.type.NativeType;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.Views;

public class ViewImageExample
{
	private static < T extends Type< T > & Comparable< T > > void getMinMax( final IterableInterval< T > source, final T minValue, final T maxValue )
	{
		for ( final T t : source )
			if ( minValue.compareTo( t ) > 0 )
				minValue.set( t );
			else if ( maxValue.compareTo( t ) < 0 )
				maxValue.set( t );
	}

	public static < T extends RealType< T > & NativeType< T > > void show( final RandomAccessibleInterval< T > interval )
	{
		final T min = Views.iterable( interval ).firstElement();
		final T max = min.copy();
		getMinMax( Views.iterable( interval ), min, max );
		show( interval, min, max );
	}

	public static < T extends RealType< T > & NativeType< T > > void show( final RandomAccessibleInterval< T > interval, final long width, final long height )
	{
		final T min = Views.iterable( interval ).firstElement();
		final T max = min.copy();
		getMinMax( Views.iterable( interval ), min, max );
		show( interval, min, max, width, height );
	}

	public static < T extends RealType< T > & NativeType< T > > void show( final RandomAccessibleInterval< T > interval, final T min, final T max )
	{
		show( interval, min, max, interval.dimension( 0 ), interval.dimension( 1 ) );
	}

	public static < T extends RealType< T > & NativeType< T > > void show( final RandomAccessibleInterval< T > interval, final T min, final T max, final long width, final long height )
	{
		final RandomAccessible< T > source = Views.extendValue( interval, min );
		final RealARGBConverter< T > converter = new RealARGBConverter< T >( min.getRealDouble(), max.getRealDouble() );
		final AffineTransform2D initial = new AffineTransform2D();
		initial.set(
			1, 0, ( width - interval.dimension( 0 ) ) / 2.0 - interval.min( 0 ),
			0, 1, ( height - interval.dimension( 1 ) ) / 2.0 - interval.min( 1 ) );
		new Interactive2DViewer< T >( ( int ) width, ( int ) height, source, converter, initial, new ArrayList< Object >() );
	}

	public static void main( final String[] args ) throws ImgIOException
	{
		final String filename = "src/test/java/resources/preikestolen.tif";
		final Img< UnsignedByteType > img = new ImgOpener().openImg( filename, new ArrayImgFactory< UnsignedByteType >(), new UnsignedByteType() );
		show( Views.interval( img, FinalInterval.createMinSize( 10, 200, 200, 200 ) ) );
	}
}
