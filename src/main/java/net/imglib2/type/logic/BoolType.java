package net.imglib2.type.logic;

import net.imglib2.type.BooleanType;

/**
 * A {@link BooleanType} wrapping a single primitive {@code boolean} variable.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class BoolType implements BooleanType< BoolType >
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
}
