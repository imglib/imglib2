package net.imglib2.newroi.util;

import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.type.logic.BoolType;

public class ContainsRandomAccess extends Point implements RandomAccess< BoolType >
{
	protected final LocalizableSet region;

	protected final BoolType type;

	public ContainsRandomAccess( final LocalizableSet region )
	{
		super( region.numDimensions() );
		this.region = region;
		type = new BoolType();
	}

	protected ContainsRandomAccess( final ContainsRandomAccess a )
	{
		super( a.numDimensions() );
		region = a.region;
		type = a.type.copy();
	}

	@Override
	public BoolType get()
	{
		type.set( region.contains( this ) );
		return type;
	}

	@Override
	public ContainsRandomAccess copy()
	{
		return new ContainsRandomAccess( this );
	}

	@Override
	public ContainsRandomAccess copyRandomAccess()
	{
		return copy();
	}
}
