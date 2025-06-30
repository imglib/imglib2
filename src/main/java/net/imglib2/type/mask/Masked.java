package net.imglib2.type.mask;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealRandomAccessible;

public interface Masked< T >
{
	T value();

	double mask();

	void setMask( double mask );

	static < T > RandomAccessibleInterval< ? extends Masked< T > > withConstant( final RandomAccessibleInterval< T > rai, final double mask )
	{
		throw new UnsupportedOperationException("NOT IMPLEMENTED. TODO.");

		// if T is RealType<T> use MaskedRealType
		// if T is Type<T> use MaskedType
		// otherwise use MaksedObj
	}

	static < T > RealRandomAccessible< ? extends Masked< T > > withConstant( final RealRandomAccessible< T > rra, final double mask )
	{
		throw new UnsupportedOperationException("NOT IMPLEMENTED. TODO.");
	}
}
