package tobias;

import net.imglib2.Interval;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converter;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.NumericType;

public class RandomAccessible2DViewer< T extends NumericType< T > >
{
	final RandomAccessible< T > source;

	final Interval interval;

	final Converter< T, ARGBType > converter;

	public RandomAccessible2DViewer( final RandomAccessible< T > source, final Interval interval, final Converter< T, ARGBType > converter )
	{
		this.source = source;
		this.interval = interval;
		this.converter = converter;
	}

	public RandomAccessible2DViewer( final RandomAccessibleInterval< T > source, final Converter< T, ARGBType > converter )
	{
		this.source = source;
		this.interval = source;
		this.converter = converter;
	}

}
