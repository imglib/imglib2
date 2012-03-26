package mpicbg.imglib.algorithm.integral;

import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.function.Converter;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.NumericType;
import mpicbg.imglib.type.numeric.integer.LongType;

/**
 * Special implementation for long using the basic type to sum up the individual lines. 
 * 
 * @author Stephan Preibisch
 *
 * @param <R>
 */
public class IntegralImageLong< R extends NumericType< R > > extends IntegralImage< R, LongType >
{

	public IntegralImageLong( final Image<R> img, final LongType type, final Converter<R, LongType> converter) 
	{
		super( img, type, converter );
	}
	
	@Override
	protected void integrateLineDim0( final Converter< R, LongType > converter, final LocalizableByDimCursor< R > cursorIn, final LocalizableByDimCursor< LongType > cursorOut, final LongType sum, final LongType tmpVar, final int size )
	{
		// compute the first pixel
		converter.convert( cursorIn.getType(), sum );
		cursorOut.getType().set( sum );
		
		long sum2 = sum.get();

		for ( int i = 2; i < size; ++i )
		{
			cursorIn.fwd( 0 );
			cursorOut.fwd( 0 );

			converter.convert( cursorIn.getType(), tmpVar );
			sum2 += tmpVar.get();
			cursorOut.getType().set( sum2 );
		}		
	}

	@Override
	protected void integrateLine( final int d, final LocalizableByDimCursor< LongType > cursor, final LongType sum, final int size )
	{
		// init sum on first pixel that is not zero
		long sum2 = cursor.getType().get();

		for ( int i = 2; i < size; ++i )
		{
			cursor.fwd( d );

			sum2 += cursor.getType().get();
			cursor.getType().set( sum2 );
		}
	}
}
