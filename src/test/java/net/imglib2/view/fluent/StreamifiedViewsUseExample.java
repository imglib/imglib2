package net.imglib2.view.fluent;

import java.util.function.Function;

import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.view.fluent.RandomAccessibleIntervalView.Extension;

public class StreamifiedViewsUseExample
{
	public static void main( String[] args )
	{
		RandomAccessibleInterval< IntType > img = ArrayImgs.ints( 100, 100 );

		final Function< RandomAccessible< ? >, String > fnRa = ra -> "accepts RandomAccessible";
		final Function< RandomAccessibleInterval< ? >, String > fnRai = rai -> "accepts RandomAccessibleInterval";

		img.view().permute( 0, 1 ).extend( Extension.border() ).use( fnRa );
		img.view().permute( 0, 1 ).use( fnRa );
//		img.view().permute( 0, 1 ).extend( Extensions.border() ).use( fnRai ); // doesn't compile
		img.view().permute( 0, 1 ).use( fnRai );

		img.view().permute( 0, 1 ).extend( Extension.border() ).use( v -> {
			RandomAccessible< ? > ra = v.delegate();
			return "lambda";
		} );
		img.view().permute( 0, 1 ).use( v -> {
			RandomAccessibleInterval< ? > rai = v.delegate();
			return "lambda";
		} );
	}
}
