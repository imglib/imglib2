package net.imglib2.view.fluent;

import java.util.function.Function;

import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.integer.IntType;

public class StreamifiedViewsApplyExample
{
	public static void main( String[] args )
	{
		RandomAccessibleInterval< IntType > img = ArrayImgs.ints( 100, 100 );

		final Function< RandomAccessible< ? >, String > fnRa = ra -> "accepts RandomAccessible";
		final Function< RandomAccessibleInterval< ? >, String > fnRai = rai -> "accepts RandomAccessibleInterval";

		img.view().permute( 0, 1 ).extendBorder().apply( fnRa );
		img.view().permute( 0, 1 ).apply( fnRa );
//		img.view().permute( 0, 1 ).extendBorder().apply( fnRai ); // doesn't compile
		img.view().permute( 0, 1 ).apply( fnRai );

		img.view().permute( 0, 1 ).extendBorder().apply( v -> {
			RandomAccessible< ? > ra = v.delegate();
			return "lambda";
		} );
		img.view().permute( 0, 1 ).apply( v -> {
			RandomAccessibleInterval< ? > rai = v.delegate();
			return "lambda";
		} );
	}
}
