package net.imglib2.view.fluent;

import net.imglib2.Interval;
import net.imglib2.RandomAccessible;
import net.imglib2.outofbounds.OutOfBoundsBorderFactory;
import net.imglib2.outofbounds.OutOfBoundsConstantValue;
import net.imglib2.outofbounds.OutOfBoundsConstantValueFactory;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.outofbounds.OutOfBoundsMirrorFactory;
import net.imglib2.outofbounds.OutOfBoundsPeriodicFactory;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.operators.SetZero;

public final class Extensions
{
	public static < T > OutOfBoundsFactory< T, RaiView< T > > border()
	{
		return new OutOfBoundsBorderFactory<>();
	}

	public static < T extends SetZero & Type< T > > OutOfBoundsFactory< T, RaiView< T > > zero()
	{
		return new OutOfBoundsZeroFactory<>();
	}

	public static < T > OutOfBoundsFactory< T, RaiView< T > > value( T value )
	{
		return new OutOfBoundsConstantValueFactory<>( value );
	}

	public static < T > OutOfBoundsFactory< T, RaiView< T > > mirrorSingle()
	{
		return new OutOfBoundsMirrorFactory<>( OutOfBoundsMirrorFactory.Boundary.SINGLE );
	}

	public static < T > OutOfBoundsFactory< T, RaiView< T > > mirrorDouble()
	{
		return new OutOfBoundsMirrorFactory<>( OutOfBoundsMirrorFactory.Boundary.DOUBLE );
	}

	public static < T > OutOfBoundsFactory< T, RaiView< T > > periodic()
	{
		return new OutOfBoundsPeriodicFactory<>();
	}

	public static void tests( RaiView< UnsignedByteType > view, RaiView< String > sv )
	{
		sv.extend( Extensions.border() );
//		sv.extend( Extensions.zero() ); // doesn't compile
		view.extend( Extensions.border() );
		view.extend( Extensions.zero() );
	}

	// TODO: move to net.imglib2.outofbounds package
	// TODO: handle in PrimitiveBlocks
	// TODO: use in Views.extendZero() and Views.expandZero()
	public static class OutOfBoundsZeroFactory< T extends SetZero & Type< T >, F extends Interval & RandomAccessible< T > >
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

	// static utility class, cannot be instantiated
	private Extensions() {}
}
