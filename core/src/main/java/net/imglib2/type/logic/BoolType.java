package net.imglib2.type.logic;

import net.imglib2.type.BooleanType;
import net.imglib2.type.numeric.integer.AbstractIntegerType;

public class BoolType extends AbstractIntegerType< BoolType > implements BooleanType< BoolType >
{
	boolean value;

	public BoolType()
	{
		this( false );
	}

	public BoolType( final boolean value )
	{
		this.value = value;
	}

	public < T extends BooleanType<T> >BoolType( final T type )
	{
		this( type.get() );
	}

	@Override
	public BoolType createVariable()
	{
		return new BoolType();
	}

	@Override
	public BoolType copy()
	{
		return new BoolType( this );
	}

	@Override
	public void set( final BoolType c )
	{
		value = c.get();
	}

	@Override
	public int getInteger()
	{
		return get() ? 1 : 0;
	}

	@Override
	public long getIntegerLong()
	{
		return get() ? 1 : 0;
	}

	@Override
	public void setInteger( final int f )
	{
		if ( f >= 1 )
			set( true );
		else
			set( false );
	}

	@Override
	public void setInteger( final long f )
	{
		if ( f >= 1 )
			set( true );
		else
			set( false );
	}

	@Override
	public double getMaxValue()
	{
		return 1;
	}

	@Override
	public double getMinValue()
	{
		return 0;
	}

	@Override
	public int compareTo( final BoolType o )
	{
		if ( value )
			return o.value ? 0 : 1;
		else
			return o.value ? -1 : 0;
	}

	@Override
	public boolean get()
	{
		return value;
	}

	@Override
	public void set( final boolean value )
	{
		this.value = value;
	}

	public void and( final boolean b )
	{
		value &= b;
	}

	public void or( final boolean b )
	{
		value |= b;
	}

	public void xor( final boolean b )
	{
		value ^= b;
	}

	@Override
	public void and( final BoolType c )
	{
		and( c.value );
	}

	@Override
	public void or( final BoolType c )
	{
		or( c.value );
	}

	@Override
	public void xor( final BoolType c )
	{
		xor( c.value );
	}

	@Override
	public void not()
	{
		value = !value;
	}

	@Override
	public void add( final BoolType c )
	{
		xor( c );
	}

	@Override
	public void div( final BoolType c )
	{
		and( c );
	}

	@Override
	public void mul( final BoolType c )
	{
		and( c );
	}

	@Override
	public void sub( final BoolType c )
	{
		xor( c );
	}

	@Override
	public void mul( final float c )
	{
		and( c >= 0.5f );
	}

	@Override
	public void mul( final double c )
	{
		and( c >= 0.5 );
	}

	@Override
	public void setOne()
	{
		value = true;
	}

	@Override
	public void setZero()
	{
		value = false;
	}

	@Override
	public void inc()
	{
		not();
	}

	@Override
	public void dec()
	{
		not();
	}

	@Override
	public String toString()
	{
		return value ? "1" : "0";
	}

	@Override
	public int getBitsPerPixel()
	{
		return 1;
	}
}
