package net.imglib2.type.mask;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealRandomAccessible;
import net.imglib2.Volatile;
import net.imglib2.type.Type;

public interface Masked< T >
{
	T value();

	void setValue( T value );

	double mask();

	void setMask( double mask );

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
