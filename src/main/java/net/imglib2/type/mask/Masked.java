package net.imglib2.type.mask;

import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealRandomAccessible;
import net.imglib2.Volatile;
import net.imglib2.type.Type;

/**
 * A {@code T} value with an associated {@code double} mask.
 * <p>
 * The maske is intended to be used as an alpha value in the range {@code [0, 1]},
 * but values are <em>not</em>> clamped to that range.
 *
 * @param <T>
 * 		the value type.
 */
public interface Masked< T >
{
	T value();

	/**
	 * If {@code T extends Type<T>} uses the {@link Type#set(Type)} method to
	 * update the internal value instance. Otherwise, updates {@link #value()}
	 * to reference the given {@code value}.
	 */
	void setValue( T value );

	double mask();

	void setMask( double mask );

	/**
	 * Augments a {@code RandomAccessibleInterval} with the given constant {@code mask}.
	 */
	@SuppressWarnings( { "unchecked", "rawtypes" } )
	static < T > RandomAccessibleInterval< ? extends Masked< T > > withConstant( final RandomAccessibleInterval< T > rai, final double mask )
	{
		T type = rai.getType();
		if ( type instanceof Type )
		{
			if ( type instanceof Volatile )
				return MaskedVolatileType.withConstant( ( RandomAccessibleInterval ) rai, mask );
			else
				return MaskedType.withConstant( ( RandomAccessibleInterval ) rai, mask );
		}
		else
		{
			return MaskedObject.withConstant( rai, mask );
		}
	}

	/**
	 * Augments a {@code RandomAccessible} with the given constant {@code mask}.
	 */
	@SuppressWarnings( { "unchecked", "rawtypes" } )
	static < T > RandomAccessible< ? extends Masked< T > > withConstant( final RandomAccessible< T > ra, final double mask )
	{
		T type = ra.getType();
		if ( type instanceof Type )
		{
			if ( type instanceof Volatile )
				return MaskedVolatileType.withConstant( ( RandomAccessible ) ra, mask );
			else
				return MaskedType.withConstant( ( RandomAccessible ) ra, mask );
		}
		else
		{
			return MaskedObject.withConstant( ra, mask );
		}
	}

	/**
	 * Augments a {@code RealRandomAccessible} with the given constant {@code mask}.
	 */
	@SuppressWarnings( { "unchecked", "rawtypes" } )
	static < T > RealRandomAccessible< ? extends Masked< T > > withConstant( final RealRandomAccessible< T > rra, final double mask )
	{
		T type = rra.getType();
		if ( type instanceof Type )
		{
			if ( type instanceof Volatile )
				return MaskedVolatileType.withConstant( ( RealRandomAccessible ) rra, mask );
			else
				return MaskedType.withConstant( ( RealRandomAccessible ) rra, mask );
		}
		else
		{
			return MaskedObject.withConstant( rra, mask );
		}
	}
}
