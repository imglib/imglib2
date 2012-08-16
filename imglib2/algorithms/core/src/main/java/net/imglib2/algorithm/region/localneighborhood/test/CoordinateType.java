package net.imglib2.algorithm.region.localneighborhood.test;

import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.type.Type;

public class CoordinateType extends Point implements Type< CoordinateType >
{
	public CoordinateType( final Localizable l )
	{
		super( l );
	}

	public CoordinateType( final int n )
	{
		super( n );
	}

	public CoordinateType( final long... position )
	{
		super( position );
	}

	@Override
	public CoordinateType createVariable()
	{
		return new CoordinateType( numDimensions() );
	}

	@Override
	public CoordinateType copy()
	{
		return new CoordinateType( this );
	}

	@Override
	public void set( final CoordinateType c )
	{
		setPosition( c );
	}
}
