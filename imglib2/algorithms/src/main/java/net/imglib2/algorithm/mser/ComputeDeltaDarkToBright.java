package net.imglib2.algorithm.mser;

import net.imglib2.type.numeric.NumericType;

public final class ComputeDeltaDarkToBright< T extends NumericType< T > > implements ComputeDeltaValue< T >
{
	private final T delta;

	ComputeDeltaDarkToBright( final T delta )
	{
		this.delta = delta;
	}

	@Override
	public T valueMinusDelta( final T value )
	{
		final T valueMinus = value.copy();
		valueMinus.sub( delta );
		return valueMinus;
	}
}