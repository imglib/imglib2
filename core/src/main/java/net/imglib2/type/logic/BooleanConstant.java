package net.imglib2.type.logic;

import net.imglib2.type.BooleanType;
import net.imglib2.type.numeric.integer.AbstractIntegerType;

public final class BooleanConstant extends AbstractIntegerType< BooleanConstant > implements BooleanType< BooleanConstant >
{
	final static public BooleanConstant True = new BooleanConstant( true );

	final static public BooleanConstant False = new BooleanConstant( false );

	final boolean value;

	private BooleanConstant( final boolean value )
	{
		this.value = value;
	}

	@Override
	public BooleanConstant createVariable()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public BooleanConstant copy()
	{
		return this;
	}

	@Override
	public void set( final BooleanConstant c )
	{
		throw new UnsupportedOperationException();
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
		throw new UnsupportedOperationException();
	}

	@Override
	public void setInteger( final long f )
	{
		throw new UnsupportedOperationException();
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
	public int compareTo( final BooleanConstant o )
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
		throw new UnsupportedOperationException();
	}

	public void and( final boolean b )
	{
		throw new UnsupportedOperationException();
	}

	public void or( final boolean b )
	{
		throw new UnsupportedOperationException();
	}

	public void xor( final boolean b )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void and( final BooleanConstant c )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void or( final BooleanConstant c )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void xor( final BooleanConstant c )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void not()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void add( final BooleanConstant c )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void div( final BooleanConstant c )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void mul( final BooleanConstant c )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void sub( final BooleanConstant c )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void mul( final float c )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void mul( final double c )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setOne()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setZero()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void inc()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void dec()
	{
		throw new UnsupportedOperationException();
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
