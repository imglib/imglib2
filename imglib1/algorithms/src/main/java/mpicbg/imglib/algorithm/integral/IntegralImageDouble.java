package mpicbg.imglib.algorithm.integral;

import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.function.Converter;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.NumericType;
import mpicbg.imglib.type.numeric.real.DoubleType;

/**
 * Special implementation for double using the basic type to sum up the individual lines. 
 * 
 * @author Stephan Preibisch
 *
 * @param <R>
 */
public class IntegralImageDouble< R extends NumericType< R > > extends IntegralImage< R, DoubleType >
{

	public IntegralImageDouble( final Image<R> img, final DoubleType type, final Converter<R, DoubleType> converter) 
	{
		super( img, type, converter );
	}

	@Override
	protected void integrateLineDim0( final Converter< R, DoubleType > converter, final LocalizableByDimCursor< R > cursorIn, final LocalizableByDimCursor< DoubleType > cursorOut, final DoubleType sum, final DoubleType tmpVar, final int size )
	{
		// compute the first pixel
		converter.convert( cursorIn.getType(), sum );
		cursorOut.getType().set( sum );
		
		double sum2 = sum.get();

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
	protected void integrateLine( final int d, final LocalizableByDimCursor< DoubleType > cursor, final DoubleType sum, final int size )
	{
		// init sum on first pixel that is not zero
		double sum2 = cursor.getType().get();

		for ( int i = 2; i < size; ++i )
		{
			cursor.fwd( d );

			sum2 += cursor.getType().get();
			cursor.getType().set( sum2 );
		}
	}
	
}
