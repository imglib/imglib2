package net.imglib2.algorithm.integral;

import net.imglib2.RandomAccess;
import net.imglib2.converter.Converter;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.integer.LongType;

/**
 * Special implementation for long using the basic type to sum up the individual lines. 
 * 
 * @author Stephan Preibisch
 *
 * @param <R>
 */
public class IntegralImgLong< R extends NumericType< R > > extends IntegralImg< R, LongType >
{

	public IntegralImgLong( final Img<R> img, final LongType type, final Converter<R, LongType> converter) 
	{
		super( img, type, converter );
	}
	
	@Override
	protected void integrateLineDim0( final Converter< R, LongType > converter, final RandomAccess< R > cursorIn, final RandomAccess< LongType > cursorOut, final LongType sum, final LongType tmpVar, final long size )
	{
		// compute the first pixel
		converter.convert( cursorIn.get(), sum );
		cursorOut.get().set( sum );
		
		long sum2 = sum.get();

		for ( int i = 2; i < size; ++i )
		{
			cursorIn.fwd( 0 );
			cursorOut.fwd( 0 );

			converter.convert( cursorIn.get(), tmpVar );
			sum2 += tmpVar.get();
			cursorOut.get().set( sum2 );
		}		
	}

	@Override
	protected void integrateLine( final int d, final RandomAccess< LongType > cursor, final LongType sum, final long size )
	{
		// init sum on first pixel that is not zero
		long sum2 = cursor.get().get();

		for ( int i = 2; i < size; ++i )
		{
			cursor.fwd( d );

			sum2 += cursor.get().get();
			cursor.get().set( sum2 );
		}
	}
}
