package net.imglib2.img.display.imagej;

import ij.ImagePlus;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converter;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.RandomAccessibleIntervalCursor;
import net.imglib2.view.Views;

public class ImageJVirtualStackFloat< S > extends ImageJVirtualStack< S, FloatType >
{
	public ImageJVirtualStackFloat( final RandomAccessibleInterval< S > source, final Converter< S, FloatType > converter )
	{
		super( source, converter, new FloatType(), ImagePlus.GRAY32 );
		setMinMax( source, converter );
	}

	public void setMinMax ( final RandomAccessibleInterval< S > source, final Converter< S, FloatType > converter )
	{
		final RandomAccessibleIntervalCursor< S > cursor = new RandomAccessibleIntervalCursor< S >( Views.isZeroMin( source ) ? source : Views.zeroMin( source ) );
		final FloatType t = new FloatType();

		if ( cursor.hasNext() )
		{
			converter.convert( cursor.next(), t );

			float min = t.get();
			float max = min;

			while ( cursor.hasNext() )
			{
				converter.convert( cursor.next(), t );
				final float value = t.get();

				if ( value < min )
					min = value;

				if ( value > max )
					max = value;
			}

			System.out.println("fmax = " + max );
			System.out.println("fmin = " + min );
			imageProcessor.setMinAndMax( min, max );
		}
	}
}
