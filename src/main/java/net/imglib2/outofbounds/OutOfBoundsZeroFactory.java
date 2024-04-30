package net.imglib2.outofbounds;

import net.imglib2.Interval;
import net.imglib2.RandomAccessible;
import net.imglib2.type.Type;
import net.imglib2.type.operators.SetZero;

/**
 * Return the zero value of {@code T} when out of bounds.
 *
 * @param <T>
 * 		pixel type
 *
 * @author Tobias Pietzsch
 */
public class OutOfBoundsZeroFactory< T extends Type< T > & SetZero, F extends Interval & RandomAccessible< T > >
		implements OutOfBoundsFactory< T, F >
{
	@Override
	public OutOfBoundsConstantValue< T > create( final F f )
	{
		final T zero = f.getType().createVariable();
		zero.setZero();
		return new OutOfBoundsConstantValue<>( f, zero );
	}
}
