package net.imglib2.newroi;
import net.imglib2.AbtractPositionableInterval;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.newroi.util.ContainsRandomAccess;
import net.imglib2.newroi.util.LocalizableSet;
import net.imglib2.type.logic.BoolType;


public class HyperSphereRegion extends AbtractPositionableInterval implements RandomAccessibleInterval< BoolType >, LocalizableSet
{
	private static Interval createBoundingBox( final int n, final double radius )
	{
		final long r = ( long )( radius + 1 );
		final long[] min = new long[ n ];
		final long[] max = new long[ n ];
		for ( int d = 0; d < n; ++ d )
		{
			min[ d ] = -r;
			max[ d ] = r;
		}
		return new FinalInterval( min, max );
	}

	protected final double squRadius;

	public HyperSphereRegion( final int numDimensions, final double radius )
	{
		super( createBoundingBox( numDimensions, radius ) );
		squRadius = radius * radius;
	}

	// TODO: RandomAccess is implemented using ContainsRandomAccess. This should
	// be changed to something more efficient.
	// Additionally randomAccess( Interval ) should check whether the interval
	// is completely inside or outside the region and return a Constant-Value
	// RandomAccess in that case.
	@Override
	public boolean contains( final Localizable p )
	{
		long squLen = 0;
		for ( int d = 0; d < n; ++d )
		{
			final long diff = p.getLongPosition( d ) - position[ d ];
			squLen += diff * diff;
		}
		return squLen <= squRadius;
	}

	@Override
	public RandomAccess< BoolType > randomAccess()
	{
		return new ContainsRandomAccess( this );
	}

	@Override
	public RandomAccess< BoolType > randomAccess( final Interval interval )
	{
		return randomAccess();
	}

}
