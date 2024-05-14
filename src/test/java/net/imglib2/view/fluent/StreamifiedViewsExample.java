package net.imglib2.view.fluent;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealRandomAccessible;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.view.fluent.RaView.Interpolation;
import net.imglib2.view.fluent.RaiView.Extension;

public class StreamifiedViewsExample
{
	public static void main( String[] args )
	{
		RandomAccessibleInterval< IntType > img = ArrayImgs.ints( 100, 100 );

		RealRandomAccessible< IntType > view = img.view()
				.extend( Extension.zero() )
				.interpolate( Interpolation.lanczos() );

		RealRandomAccessible< DoubleType > doubleView = img.view()
				.extend( Extension.zero() )
				.permute( 0, 1 )
				.convert( DoubleType::new, ( i, o ) -> o.set( i.get() ) )
				.interpolate( Interpolation.lanczos() );

		RandomAccess< IntType > access = img.view()
				.extend( Extension.border() )
				.interpolate( Interpolation.nLinear() )
				.raster()
				.interval( img )
				.randomAccess();

		Cursor< IntType > cursor = img.view()
				.interval( img )
				.cursor();
	}
}
