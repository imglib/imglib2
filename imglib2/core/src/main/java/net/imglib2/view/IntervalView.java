package net.imglib2.view;

import net.imglib2.AbstractInterval;
import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;

public class IntervalView< T > extends AbstractInterval implements RandomAccessibleInterval< T >
{
	protected final RandomAccessible< T > source;
	
	protected RandomAccessible< T > fullViewRandomAccessible;
	
	public IntervalView( RandomAccessible< T > source, final Interval interval )
	{
		super( interval );
		assert( source.numDimensions() == interval.numDimensions() );

		this.source = source;
		this.fullViewRandomAccessible = null;		
	}

	public IntervalView( RandomAccessible< T > source, final long[] min, final long[] max )
	{
		super( min, max );
		assert( source.numDimensions() == min.length );

		this.source = source;
		this.fullViewRandomAccessible = null;		
	}

	public RandomAccessible< T > getSource()
	{
		return source;
	}

	@Override
	public RandomAccess< T > randomAccess( Interval interval )
	{
		return TransformBuilder.getEfficientRandomAccessible( interval, this ).randomAccess(); 
	}

	@Override
	public RandomAccess< T > randomAccess()
	{
		if ( fullViewRandomAccessible == null )
			fullViewRandomAccessible = TransformBuilder.getEfficientRandomAccessible( this, this ); 
		return fullViewRandomAccessible.randomAccess();
	}
}
