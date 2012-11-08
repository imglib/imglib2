package net.imglib2.newroi;

import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.newroi.util.AbtractPositionableInterval;
import net.imglib2.newroi.util.ContainsRandomAccess;
import net.imglib2.newroi.util.LocalizableSet;
import net.imglib2.type.logic.BoolType;
import net.imglib2.util.Intervals;

public class HyperBoxRegion extends AbtractPositionableInterval implements RandomAccessibleInterval< BoolType >, LocalizableSet
{
	public HyperBoxRegion( final Interval interval )
	{
		super( interval );
	}

	// TODO: RandomAccess is implemented using ContainsRandomAccess. This should
	// be changed to something more efficient. Implementation would look similar
	// to AbstractOutOfBoundsValue.
	// Additionally randomAccess( Interval ) should check whether the interval
	// is completely inside or outside the region and return a Constant-Value
	// RandomAccess in that case.
	@Override
	public boolean contains( final Localizable p )
	{
		return Intervals.contains( this, p );
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
