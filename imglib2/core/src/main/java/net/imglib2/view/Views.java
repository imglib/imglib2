package net.imglib2.view;

import net.imglib2.ExtendedRandomAccessibleInterval;
import net.imglib2.Interval;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.outofbounds.OutOfBoundsConstantValueFactory;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.outofbounds.OutOfBoundsMirrorFactory;
import net.imglib2.outofbounds.OutOfBoundsPeriodicFactory;
import net.imglib2.transform.integer.MixedTransform;
import net.imglib2.type.Type;

public class Views
{
	/**
	 * Extend a RandomAccessibleInterval with an out-of-bounds strategy.
	 * 
	 * @param randomAccessible
	 *            the interval to extend.
	 * @param factory
	 *            the out-of-bounds strategy.
	 * @return (unbounded) RandomAccessible which extends the input interval to
	 *         infinity.
	 */
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

	/**
	 * Define an interval on a RandomAccessible. It is the callers
	 * responsibility to ensure that the source RandomAccessible is defined in
	 * the specified interval.
	 * 
	 * @param randomAccessible
	 *            the source
	 * @param min
	 *            lower bound of interval
	 * @param max
	 *            upper bound of interval
	 * @return a RandomAccessibleInterval
	 */
	public static < T > IntervalView< T > interval( final RandomAccessible< T > randomAccessible, long[] min, long[] max )
	{
		return new IntervalView< T >( randomAccessible, min, max );
	}

	/**
	 * Define an interval on a RandomAccessible. It is the callers
	 * responsibility to ensure that the source RandomAccessible is defined in
	 * the specified interval.
	 * 
	 * @param randomAccessible
	 *            the source
	 * @param interval
	 *            interval boundaries.
	 * @return a RandomAccessibleInterval
	 */
	public static < T > IntervalView< T > interval( final RandomAccessible< T > randomAccessible, final Interval interval )
	{
		return new IntervalView< T >( randomAccessible, interval );
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
	public static < T > MixedTransformView< T > rotate( final RandomAccessible< T > randomAccessible, final int fromAxis, final int toAxis )
	{
		final int n = randomAccessible.numDimensions();
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
		t.setComponentMapping( component );
		t.setComponentInversion( inv );
		return new MixedTransformView< T >( randomAccessible, t );
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
	public static < T > IntervalView< T > rotate( final RandomAccessibleInterval< T > interval, final int fromAxis, final int toAxis )
	{
		final int n = interval.numDimensions();
		final long[] min = new long[ n ];
		final long[] max = new long[ n ];
		interval.min( min );
		interval.max( max );
		final long fromMinNew = - max[ toAxis ];
		final long fromMaxNew = - min[ toAxis ];
		min[ toAxis ] = min[ fromAxis ];
		max[ toAxis ] = max[ fromAxis ];
		min[ fromAxis ] = fromMinNew;
		max[ fromAxis ] = fromMaxNew;
		return interval( rotate( ( RandomAccessible< T > ) interval, fromAxis, toAxis ), min, max );
	}

	/**
	 * Translate such that pixel at offset in randomAccessible is
	 * pixel 0 in the resulting view.
	 * 
	 * @param randomAccessible
	 *            the source
	 */
	public static < T > MixedTransformView< T > translate( final RandomAccessible< T > randomAccessible, final long[] offset )
	{
		final int n = randomAccessible.numDimensions();
		MixedTransform t = new MixedTransform( n, n );
		t.setTranslation( offset );
		return new MixedTransformView< T >( randomAccessible, t );
	}

	/**
	 * Translate such that pixel at offset in interval is
	 * pixel 0 in the resulting view.
	 * 
	 * @param randomAccessible
	 *            the source
	 */
	public static < T > IntervalView< T > translate( final RandomAccessibleInterval< T > interval, final long[] offset )
	{
		final int n = interval.numDimensions();
		long[] min = new long[ n ];
		long[] max = new long[ n ];
		interval.min( min );
		interval.max( max );
		for ( int d = 0; d < n; ++d )
		{
			min[ d ] -= offset[ d ];
			max[ d ] -= offset[ d ];
		}
		return interval( translate( ( RandomAccessible< T > ) interval, offset ), min, max );
	}

	/**
	 * Translate the source such that the upper left corner is at the origin
	 * 
	 * @param interval
	 *            the source.
	 * @return view of the source translated to the origin
	 */
	public static < T > IntervalView< T > zeroMin( final RandomAccessibleInterval< T > interval )
	{
		final int n = interval.numDimensions();
		final long[] min = new long[ n ];
		final long[] max = new long[ n ];
		final long[] offset = new long[ n ];
		interval.min( offset );
		interval.max( max );
		for ( int d = 0; d < n; ++d )
			max[ d ] -= offset[ d ];
		MixedTransform t = new MixedTransform( n, n );
		t.setTranslation( offset );
		return interval( new MixedTransformView< T >( interval, t ), min, max );
	}

	/**
	 * take a (n-1)-dimensional slice of a n-dimensional view, fixing
	 * d-component of coordinates to pos.
	 */
	public static < T > MixedTransformView< T > hyperSlice( final RandomAccessible< T > view, final int d, long pos )
	{
		final int m = view.numDimensions();
		final int n = m - 1;
		MixedTransform t = new MixedTransform( n, m );
		long[] translation = new long[ m ];
		translation[ d ] = pos;
		boolean[] zero = new boolean[ m ];
		int[] component = new int[ m ];
		for ( int e = 0; e < m; ++e )
		{
			if ( e < d )
			{
				zero[ e ] = false;
				component[ e ] = e;
			}
			else if ( e > d )
			{
				zero[ e ] = false;
				component[ e ] = e - 1;
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
		return new MixedTransformView< T >( view, t );
	}

	/**
	 * take a (n-1)-dimensional slice of a n-dimensional view, fixing
	 * d-component of coordinates to pos.
	 */
	public static < T > IntervalView< T > hyperSlice( final RandomAccessibleInterval< T > view, final int d, long pos )
	{
		final int m = view.numDimensions();
		final int n = m - 1;
		long[] min = new long[ n ];
		long[] max = new long[ n ];
		for ( int e = 0; e < m; ++e )
		{
			if ( e < d )
			{
				min[ e ] = view.min( e );
				max[ e ] = view.max( e );
			}
			else if ( e > d )
			{
				min[ e - 1] = view.min( e );
				max[ e - 1] = view.max( e );
			}
		}
		return interval( hyperSlice( ( RandomAccessible< T > ) view, d, pos), min, max );
	}

	/**
	 * Invert the d-axis.
	 * 
	 * @param randomAccessible
	 *            the source
	 * @param d
	 *            the axis to invert
	 */
	public static < T > MixedTransformView< T > invertAxis( final RandomAccessible< T > randomAccessible, final int d )
	{
		final int n = randomAccessible.numDimensions();
		boolean[] inv = new boolean[ n ];
		inv[ d ] = true;
		MixedTransform t = new MixedTransform( n, n );
		t.setComponentInversion( inv );
		return new MixedTransformView< T >( randomAccessible, t );
	}

	/**
	 * Invert the d-axis.
	 * 
	 * @param interval
	 *            the source
	 * @param d
	 *            the axis to invert
	 */
	public static < T > IntervalView< T > invertAxis( final RandomAccessibleInterval< T > interval, final int d )
	{
		final int n = interval.numDimensions();
		long[] min = new long[ n ];
		long[] max = new long[ n ];
		interval.min( min );
		interval.max( max );
		long tmp = min[ d ];
		min[ d ] = - max[ d ];
		max[ d ] = - tmp;
		return interval( invertAxis( ( RandomAccessible< T > ) interval, d ), min, max );
	}

	
	
	
	/**
	 * Define an interval on a RandomAccessible and translate it such that the
	 * min corner is at the origin. It is the callers responsibility to ensure
	 * that the source RandomAccessible is defined in the specified interval.
	 * 
	 * @param randomAccessible
	 *            the source
	 * @param offset
	 *            offset of min corner.
	 * @param dimension
	 *            size of the interval.
	 * @return a RandomAccessibleInterval
	 */
	public static < T > IntervalView< T > offsetInterval( final RandomAccessible< T > randomAccessible, long[] offset, long[] dimension )
	{
		final int n = randomAccessible.numDimensions();
		final long[] min = new long[ n ];
		final long[] max = new long[ n ];
		for ( int d = 0; d < n; ++d )
			max[ d ] = dimension[ d ] - 1;
		return interval( translate( randomAccessible, offset ), min, max );
	}


	/**
	 * Invert the d-axis and shift the resulting view to the origin.
	 * 
	 * @param interval
	 *            the source
	 * @param d
	 *            the axis to invert
	 */
	@Deprecated
	public static < T > IntervalView< T > flippedView( final RandomAccessibleInterval< T > interval, final int d )
	{
		return zeroMin( invertAxis( interval, d ) );
	}

	/**
	 * Create view that is rotated by 90 degrees and shifted to the origin. The
	 * rotation is specified by the fromAxis and toAxis arguments.
	 *
	 * If fromAxis=0 and toAxis=1, this means that the X-axis of the source view
	 * is mapped to the Y-Axis of the rotated view. That is, it corresponds to a
	 * 90 degree clock-wise rotation of the source view in the XY plane.
	 *
	 * fromAxis=1 and toAxis=0 corresponds to a counter-clock-wise rotation in
	 * the XY plane.
	 * 
	 * @param interval
	 *            the source view
	 * @param fromAxis
	 *            axis of interval which is to be mapped to toAxis of target
	 *            view
	 * @param toAxis
	 *            axis of target view to which mapped to fromAxis of interval is
	 *            mapped
	 */
	@Deprecated
	public static < T > IntervalView< T > rotatedView( final RandomAccessibleInterval< T > interval, final int fromAxis, final int toAxis )
	{
		return zeroMin( rotate( interval, fromAxis, toAxis ) );
	}

	/**
	 * Define an interval on a RandomAccessible and translate it such that the
	 * min corner is at the origin. It is the callers responsibility to ensure
	 * that the source RandomAccessible is defined in the specified interval.
	 * 
	 * @param randomAccessible
	 *            the source
	 * @param offset
	 *            offset of min corner.
	 * @param dimension
	 *            size of the interval.
	 * @return a RandomAccessibleInterval
	 */
	@Deprecated
	public static < T > IntervalView< T > superIntervalView( final RandomAccessible< T > randomAccessible, long[] offset, long[] dimension )
	{
		return offsetInterval( randomAccessible, offset, dimension );
	}
}
