package net.imglib2.view;

import net.imglib2.ExtendedRandomAccessibleInterval;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.outofbounds.OutOfBoundsConstantValueFactory;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.outofbounds.OutOfBoundsMirrorFactory;
import net.imglib2.outofbounds.OutOfBoundsMirrorSingleBoundary;
import net.imglib2.outofbounds.OutOfBoundsPeriodicFactory;
import net.imglib2.transform.integer.MixedTransform;
import net.imglib2.type.Type;
import net.imglib2.util.Util;

public class Views
{
	public static < T, F extends RandomAccessibleInterval< T > > ExtendedRandomAccessibleInterval< T, F > extend( final F randomAccessible, final OutOfBoundsFactory< T, ? super F > factory )
	{
		return new ExtendedRandomAccessibleInterval< T, F >( randomAccessible, factory );
	}

	/**
	 * Extend a RandomAccessibleInterval with a mirroring out-of-bounds
	 * strategy. Boundary pixels are not repeated. {@see
	 * OutOfBoundsMirrorSingleBoundary}.
	 * 
	 * @param randomAccessible
	 *            the interval to extend.
	 * @return (unbounded) RandomAccessible which extends the input interval to
	 *         infinity.
	 */
	public static < T, F extends RandomAccessibleInterval< T > > ExtendedRandomAccessibleInterval< T, F > extendMirrorSingle( final F randomAccessible )
	{
		return new ExtendedRandomAccessibleInterval< T, F >( randomAccessible, new OutOfBoundsMirrorFactory< T, F >( OutOfBoundsMirrorFactory.Boundary.SINGLE ) );
	}

	/**
	 * Extend a RandomAccessibleInterval with a mirroring out-of-bounds
	 * strategy. Boundary pixels are repeated. {@see
	 * OutOfBoundsMirrorDoubleBoundary}.
	 * 
	 * @param randomAccessible
	 *            the interval to extend.
	 * @return (unbounded) RandomAccessible which extends the input interval to
	 *         infinity.
	 */
	public static < T, F extends RandomAccessibleInterval< T > > ExtendedRandomAccessibleInterval< T, F > extendMirrorDouble( final F randomAccessible )
	{
		return new ExtendedRandomAccessibleInterval< T, F >( randomAccessible, new OutOfBoundsMirrorFactory< T, F >( OutOfBoundsMirrorFactory.Boundary.DOUBLE ) );
	}

	/**
	 * Extend a RandomAccessibleInterval with a constant-value out-of-bounds
	 * strategy. {@see OutOfBoundsConstantValue}.
	 * 
	 * @param randomAccessible
	 *            the interval to extend.
	 * @return (unbounded) RandomAccessible which extends the input interval to
	 *         infinity.
	 */
	public static < T extends Type< T >, F extends RandomAccessibleInterval< T > > ExtendedRandomAccessibleInterval< T, F > extendValue( final F randomAccessible, final T value )
	{
		return new ExtendedRandomAccessibleInterval< T, F >( randomAccessible, new OutOfBoundsConstantValueFactory< T, F >( value ) );
	}

	/**
	 * Extend a RandomAccessibleInterval with a periodic out-of-bounds
	 * strategy. {@see OutOfBoundsPeriodic}.
	 * 
	 * @param randomAccessible
	 *            the interval to extend.
	 * @return (unbounded) RandomAccessible which extends the input interval to
	 *         infinity.
	 */
	public static < T, F extends RandomAccessibleInterval< T > > ExtendedRandomAccessibleInterval< T, F > extendPeriodic( final F randomAccessible )
	{
		return new ExtendedRandomAccessibleInterval< T, F >( randomAccessible, new OutOfBoundsPeriodicFactory< T, F >() );
	}

	public static < T > MixedTransformView< T > superIntervalView( final RandomAccessible< T > randomAccessible, long[] offset, long[] dimension )
	{
		final int n = randomAccessible.numDimensions();
		MixedTransform t = new MixedTransform( n, n );
		t.setTranslation( offset );
		return new MixedTransformView< T >( randomAccessible, t, dimension );
	}

	public static < T > MixedTransformView< T > flippedView( final RandomAccessibleInterval< T > interval, final int d )
	{
		final int n = interval.numDimensions();
		long[] tmp = new long[ n ];
		tmp[ d ] = interval.max( d );
		int[] component = new int[ n ];
		boolean[] inv = new boolean[ n ];
		for ( int e = 0; e < n; ++e )
		{
			component[ e ] = e;
			inv[ e ] = ( e == d );
		}
		MixedTransform t = new MixedTransform( n, n );
		t.setTranslation( tmp );
		t.setComponentMapping( component );
		t.setComponentInversion( inv );
		interval.dimensions( tmp );
		System.out.println( Util.printCoordinates( tmp ) );
		return new MixedTransformView< T >( interval, t, tmp );
	}

	/**
	 * take a (n-1)-dimensional slice of a n-dimensional view, fixing
	 * d-component of coordinates to pos.
	 * 
	 * TODO: this should work on general views, not just IntervalView
	 */
	public static < T > MixedTransformView< T > hyperSlice( final RandomAccessibleInterval< T > view, final int d, long pos )
	{
		final int m = view.numDimensions();
		final int n = m - 1;
		MixedTransform t = new MixedTransform( n, m );
		long[] translation = new long[ m ];
		translation[ d ] = pos;
		boolean[] zero = new boolean[ m ];
		int[] component = new int[ m ];
		long[] dimension = new long[ n ];
		for ( int e = 0; e < m; ++e )
		{
			if ( e < d )
			{
				zero[ e ] = false;
				component[ e ] = e;
				dimension[ e ] = view.dimension( e );
			}
			else if ( e > d )
			{
				zero[ e ] = false;
				component[ e ] = e - 1;
				dimension[ e - 1 ] = view.dimension( e );
			}
			else
			{
				zero[ e ] = true;
				component[ e ] = 0;
			}
		}
		t.setTranslation( translation );
		t.setComponentZero( zero );
		t.setComponentMapping( component );
		return new MixedTransformView< T >( view, t, dimension );
	}

	/**
	 * Create view that is rotated by 90 degrees. The rotation is specified by
	 * the fromAxis and toAxis arguments.
	 * 
	 * If fromAxis=0 and toAxis=1, this means that the X-axis of the source view
	 * is mapped to the Y-Axis of the rotated view. That is, it corresponds to a
	 * 90 degree clock-wise rotation of the source view in the XY plane.
	 * 
	 * fromAxis=1 and toAxis=0 corresponds to a counter-clock-wise rotation in
	 * the XY plane.
	 */
	public static < T > MixedTransformView< T > rotatedView( final RandomAccessibleInterval< T > interval, final int fromAxis, final int toAxis )
	{
		final int n = interval.numDimensions();
		long[] tmp = new long[ n ];
		tmp[ toAxis ] = interval.max( toAxis );
		int[] component = new int[ n ];
		boolean[] inv = new boolean[ n ];
		for ( int e = 0; e < n; ++e )
		{
			if ( e == toAxis )
			{
				component[ e ] = fromAxis;
				inv[ e ] = true;
			}
			else if ( e == fromAxis )
			{
				component[ e ] = toAxis;
			}
			else
			{
				component[ e ] = e;
			}
		}
		MixedTransform t = new MixedTransform( n, n );
		t.setTranslation( tmp );
		t.setComponentMapping( component );
		t.setComponentInversion( inv );
		interval.dimensions( tmp );
		final long fromDim = tmp[ toAxis ];
		tmp[ toAxis ] = tmp[ fromAxis ];
		tmp[ fromAxis ] = fromDim;
		return new MixedTransformView< T >( interval, t, tmp );
	}
}
