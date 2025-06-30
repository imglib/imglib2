package net.imglib2.type.volatiles;

import java.util.Objects;

import net.imglib2.Volatile;
import net.imglib2.util.Cast;
import net.imglib2.util.Util;

/**
 * Something volatile that has a value and is either VALID or INVALID.
 *
 * @author Stephan Saalfeld
 */
public class AbstractVolatile< T > implements Volatile< T >
{
	final T t;

	boolean valid;

	public AbstractVolatile( final T t, final boolean valid )
	{
		this.t = t;
		this.valid = valid;
	}

	public AbstractVolatile( final T t )
	{
		this( t, false );
	}

	// --- Volatile<T> ---

	@Override
	public T get()
	{
		return t;
	}

	@Override
	public boolean isValid()
	{
		return valid;
	}

	@Override
	public void setValid( final boolean valid )
	{
		this.valid = valid;
	}

	// --- Object ---

	@Override
	public boolean equals( final Object obj )
	{
		if ( ! getClass().isInstance( obj ) )
			return false;
		final Volatile< T > other = Cast.unchecked( obj );
		return other.isValid() == isValid() && Objects.equals( other.get(), get() );
	}

	@Override
	public int hashCode()
	{
		return Util.combineHash( Boolean.hashCode( isValid() ), Objects.hashCode( get() ) );
	}
}
