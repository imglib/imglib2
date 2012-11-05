package net.imglib2.newroi;

import net.imglib2.AbstractInterval;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.newroi.util.ContainsRandomAccess;
import net.imglib2.newroi.util.LocalizableSet;
import net.imglib2.type.logic.BoolType;
import net.imglib2.util.Intervals;

public class HyperBoxRegion extends AbstractInterval implements RandomAccessibleInterval< BoolType >, LocalizableSet
{
	public HyperBoxRegion( final Interval interval )
	{
		super( interval );
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

	@Override
	public boolean contains( final Localizable p )
	{
		return Intervals.contains( this, p );
	}
}
