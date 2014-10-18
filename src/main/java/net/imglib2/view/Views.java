/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package net.imglib2.view;

import net.imglib2.EuclideanSpace;
import net.imglib2.FlatIterationOrder;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealRandomAccessible;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.interpolation.Interpolant;
import net.imglib2.interpolation.InterpolatorFactory;
import net.imglib2.outofbounds.OutOfBoundsBorderFactory;
import net.imglib2.outofbounds.OutOfBoundsConstantValueFactory;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.outofbounds.OutOfBoundsMirrorFactory;
import net.imglib2.outofbounds.OutOfBoundsPeriodicFactory;
import net.imglib2.outofbounds.OutOfBoundsRandomValueFactory;
import net.imglib2.transform.integer.MixedTransform;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Util;
import net.imglib2.view.composite.CompositeIntervalView;
import net.imglib2.view.composite.CompositeView;
import net.imglib2.view.composite.GenericComposite;
import net.imglib2.view.composite.NumericComposite;
import net.imglib2.view.composite.RealComposite;

/**
 * Create light-weight views into {@link RandomAccessible RandomAccessibles}.
 * 
 * A view is itself a {@link RandomAccessible} or
 * {@link RandomAccessibleInterval} that provides {@link RandomAccess accessors}
 * that transform coordinates on-the-fly without copying the underlying data.
 * Consecutive transformations are concatenated and simplified to provide
 * optimally efficient accessors. Note, that accessors provided by a view are
 * read/write. Changing pixels in a view changes the underlying image data.
 * 
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 * @author Stephan Saalfeld
 */
public class Views
{
	/**
	 * Returns a {@link RealRandomAccessible} using interpolation
	 * 
	 * @param source
	 *            the {@link EuclideanSpace} to be interpolated
	 * @param factory
	 *            the {@link InterpolatorFactory} to provide interpolators for
	 *            source
	 * @return
	 */
	public static < T, F extends EuclideanSpace > RealRandomAccessible< T > interpolate( final F source, final InterpolatorFactory< T, F > factory )
	{
		return new Interpolant< T, F >( source, factory );
	}

	/**
	 * Turns a {@link RealRandomAccessible} into a {@link RandomAccessible},
	 * providing {@link RandomAccess} at integer coordinates.
	 * 
	 * @see #interpolate(net.imglib2.EuclideanSpace,
	 *      net.imglib2.interpolation.InterpolatorFactory)
	 * 
	 * @param source
	 *            the {@link RealRandomAccessible} to be rasterized.
	 * @return a {@link RandomAccessibleOnRealRandomAccessible} wrapping source.
	 */
	public static < T > RandomAccessibleOnRealRandomAccessible< T > raster( final RealRandomAccessible< T > source )
	{
		return new RandomAccessibleOnRealRandomAccessible< T >( source );
	}

	/**
	 * Extend a RandomAccessibleInterval with an out-of-bounds strategy.
	 * 
	 * @param source
	 *            the interval to extend.
	 * @param factory
	 *            the out-of-bounds strategy.
	 * @return (unbounded) RandomAccessible which extends the input interval to
	 *         infinity.
	 */
	public static < T, F extends RandomAccessibleInterval< T > > ExtendedRandomAccessibleInterval< T, F > extend( final F source, final OutOfBoundsFactory< T, ? super F > factory )
	{
		return new ExtendedRandomAccessibleInterval< T, F >( source, factory );
	}

	/**
	 * Extend a RandomAccessibleInterval with a mirroring out-of-bounds
	 * strategy. Boundary pixels are not repeated.  Note that this requires
	 * that all dimensions of the source (F source) must be &gt; 1.
	 * 
	 * @param source
	 *            the interval to extend.
	 * @return (unbounded) RandomAccessible which extends the input interval to
	 *         infinity.
	 * @see net.imglib2.outofbounds.OutOfBoundsMirrorSingleBoundary
	 */
	public static < T, F extends RandomAccessibleInterval< T > > ExtendedRandomAccessibleInterval< T, F > extendMirrorSingle( final F source )
	{
		return new ExtendedRandomAccessibleInterval< T, F >( source, new OutOfBoundsMirrorFactory< T, F >( OutOfBoundsMirrorFactory.Boundary.SINGLE ) );
	}

	/**
	 * Extend a RandomAccessibleInterval with a mirroring out-of-bounds
	 * strategy. Boundary pixels are repeated.
	 * 
	 * @param source
	 *            the interval to extend.
	 * @return (unbounded) RandomAccessible which extends the input interval to
	 *         infinity.
	 * @see net.imglib2.outofbounds.OutOfBoundsMirrorDoubleBoundary
	 */
	public static < T, F extends RandomAccessibleInterval< T > > ExtendedRandomAccessibleInterval< T, F > extendMirrorDouble( final F source )
	{
		return new ExtendedRandomAccessibleInterval< T, F >( source, new OutOfBoundsMirrorFactory< T, F >( OutOfBoundsMirrorFactory.Boundary.DOUBLE ) );
	}

	/**
	 * Extend a RandomAccessibleInterval with a constant-value out-of-bounds
	 * strategy.
	 * 
	 * @param source
	 *            the interval to extend.
	 * @return (unbounded) RandomAccessible which extends the input interval to
	 *         infinity.
	 * @see net.imglib2.outofbounds.OutOfBoundsConstantValue
	 */
	public static < T extends Type< T >, F extends RandomAccessibleInterval< T > > ExtendedRandomAccessibleInterval< T, F > extendValue( final F source, final T value )
	{
		return new ExtendedRandomAccessibleInterval< T, F >( source, new OutOfBoundsConstantValueFactory< T, F >( value ) );
	}

	/**
	 * Extend a RandomAccessibleInterval with a constant-value out-of-bounds
	 * strategy where the constant value is the zero-element of the data type.
	 * 
	 * @param source
	 *            the interval to extend.
	 * @return (unbounded) RandomAccessible which extends the input interval to
	 *         infinity with a constant value of zero.
	 * @see net.imglib2.outofbounds.OutOfBoundsConstantValue
	 */
	public static < T extends NumericType< T >, F extends RandomAccessibleInterval< T > > ExtendedRandomAccessibleInterval< T, F > extendZero( final F source )
	{
		final T zero = Util.getTypeFromInterval( source ).createVariable();
		zero.setZero();
		return new ExtendedRandomAccessibleInterval< T, F >( source, new OutOfBoundsConstantValueFactory< T, F >( zero ) );
	}

	/**
	 * Extend a RandomAccessibleInterval with a random-value out-of-bounds
	 * strategy.
	 * 
	 * @param source
	 *            the interval to extend.
	 * @param min
	 *            the minimal random value
	 * @param max
	 *            the maximal random value
	 * @return (unbounded) RandomAccessible which extends the input interval to
	 *         infinity.
	 * @see net.imglib2.outofbounds.OutOfBoundsRandomValue
	 */
	public static < T extends RealType< T >, F extends RandomAccessibleInterval< T > > ExtendedRandomAccessibleInterval< T, F > extendRandom( final F source, final double min, final double max )
	{
		return new ExtendedRandomAccessibleInterval< T, F >( source, new OutOfBoundsRandomValueFactory< T, F >( Util.getTypeFromInterval( source ), min, max ) );
	}

	/**
	 * Extend a RandomAccessibleInterval with a periodic out-of-bounds strategy.
	 * 
	 * @param source
	 *            the interval to extend.
	 * @return (unbounded) RandomAccessible which extends the input interval to
	 *         infinity.
	 * @see net.imglib2.outofbounds.OutOfBoundsPeriodic
	 */
	public static < T, F extends RandomAccessibleInterval< T > > ExtendedRandomAccessibleInterval< T, F > extendPeriodic( final F source )
	{
		return new ExtendedRandomAccessibleInterval< T, F >( source, new OutOfBoundsPeriodicFactory< T, F >() );
	}

	/**
	 * Extend a RandomAccessibleInterval with an out-of-bounds strategy to
	 * repeat border pixels.
	 * 
	 * @param source
	 *            the interval to extend.
	 * @return (unbounded) RandomAccessible which extends the input interval to
	 *         infinity.
	 * @see net.imglib2.outofbounds.OutOfBoundsBorder
	 */
	public static < T, F extends RandomAccessibleInterval< T > > ExtendedRandomAccessibleInterval< T, F > extendBorder( final F source )
	{
		return new ExtendedRandomAccessibleInterval< T, F >( source, new OutOfBoundsBorderFactory< T, F >() );
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
	public static < T > IntervalView< T > interval( final RandomAccessible< T > randomAccessible, final long[] min, final long[] max )
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
		final int[] component = new int[ n ];
		final boolean[] inv = new boolean[ n ];
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
		final MixedTransform t = new MixedTransform( n, n );
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
		final long fromMinNew = -max[ toAxis ];
		final long fromMaxNew = -min[ toAxis ];
		min[ toAxis ] = min[ fromAxis ];
		max[ toAxis ] = max[ fromAxis ];
		min[ fromAxis ] = fromMinNew;
		max[ fromAxis ] = fromMaxNew;
		return interval( rotate( ( RandomAccessible< T > ) interval, fromAxis, toAxis ), min, max );
	}

	/**
	 * Create view with permuted axes. fromAxis and toAxis are swapped.
	 * 
	 * If fromAxis=0 and toAxis=2, this means that the X-axis of the source view
	 * is mapped to the Z-Axis of the permuted view and vice versa. For a XYZ
	 * source, a ZYX view would be created.
	 */
	public static < T > MixedTransformView< T > permute( final RandomAccessible< T > randomAccessible, final int fromAxis, final int toAxis )
	{
		final int n = randomAccessible.numDimensions();
		final int[] component = new int[ n ];
		for ( int e = 0; e < n; ++e )
			component[ e ] = e;
		component[ fromAxis ] = toAxis;
		component[ toAxis ] = fromAxis;
		final MixedTransform t = new MixedTransform( n, n );
		t.setComponentMapping( component );
		return new MixedTransformView< T >( randomAccessible, t );
	}

	/**
	 * Create view with permuted axes. fromAxis and toAxis are swapped.
	 * 
	 * If fromAxis=0 and toAxis=2, this means that the X-axis of the source view
	 * is mapped to the Z-Axis of the permuted view and vice versa. For a XYZ
	 * source, a ZYX view would be created.
	 */
	public static < T > IntervalView< T > permute( final RandomAccessibleInterval< T > interval, final int fromAxis, final int toAxis )
	{
		final int n = interval.numDimensions();
		final long[] min = new long[ n ];
		final long[] max = new long[ n ];
		interval.min( min );
		interval.max( max );
		final long fromMinNew = min[ toAxis ];
		final long fromMaxNew = max[ toAxis ];
		min[ toAxis ] = min[ fromAxis ];
		max[ toAxis ] = max[ fromAxis ];
		min[ fromAxis ] = fromMinNew;
		max[ fromAxis ] = fromMaxNew;
		return interval( permute( ( RandomAccessible< T > ) interval, fromAxis, toAxis ), min, max );
	}

	/**
	 * Translate the source view by the given translation vector. Pixel
	 * <em>x</em> in the source view has coordinates <em>(x + translation)</em>
	 * in the resulting view.
	 * 
	 * @param randomAccessible
	 *            the source
	 * @param translation
	 *            translation vector of the source view. The pixel at <em>x</em>
	 *            in the source view becomes <em>(x + translation)</em> in the
	 *            resulting view.
	 */
	public static < T > MixedTransformView< T > translate( final RandomAccessible< T > randomAccessible, final long... translation )
	{
		final int n = randomAccessible.numDimensions();
		final MixedTransform t = new MixedTransform( n, n );
		t.setInverseTranslation( translation );
		return new MixedTransformView< T >( randomAccessible, t );
	}

	/**
	 * Translate the source view by the given translation vector. Pixel
	 * <em>x</em> in the source view has coordinates <em>(x + translation)</em>
	 * in the resulting view.
	 * 
	 * @param interval
	 *            the source
	 * @param translation
	 *            translation vector of the source view. The pixel at <em>x</em>
	 *            in the source view becomes <em>(x + translation)</em> in the
	 *            resulting view.
	 */
	public static < T > IntervalView< T > translate( final RandomAccessibleInterval< T > interval, final long... translation )
	{
		final int n = interval.numDimensions();
		final long[] min = new long[ n ];
		final long[] max = new long[ n ];
		interval.min( min );
		interval.max( max );
		for ( int d = 0; d < n; ++d )
		{
			min[ d ] += translation[ d ];
			max[ d ] += translation[ d ];
		}
		return interval( translate( ( RandomAccessible< T > ) interval, translation ), min, max );
	}

	/**
	 * Translate such that pixel at offset in randomAccessible is at the origin
	 * in the resulting view. This is equivalent to translating by -offset.
	 * 
	 * @param randomAccessible
	 *            the source
	 * @param offset
	 *            offset of the source view. The pixel at offset becomes the
	 *            origin of resulting view.
	 */
	public static < T > MixedTransformView< T > offset( final RandomAccessible< T > randomAccessible, final long... offset )
	{
		final int n = randomAccessible.numDimensions();
		final MixedTransform t = new MixedTransform( n, n );
		t.setTranslation( offset );
		return new MixedTransformView< T >( randomAccessible, t );
	}

	/**
	 * Translate such that pixel at offset in interval is at the origin in the
	 * resulting view. This is equivalent to translating by -offset.
	 * 
	 * @param interval
	 *            the source
	 * @param offset
	 *            offset of the source view. The pixel at offset becomes the
	 *            origin of resulting view.
	 */
	public static < T > IntervalView< T > offset( final RandomAccessibleInterval< T > interval, final long... offset )
	{
		final int n = interval.numDimensions();
		final long[] min = new long[ n ];
		final long[] max = new long[ n ];
		interval.min( min );
		interval.max( max );
		for ( int d = 0; d < n; ++d )
		{
			min[ d ] -= offset[ d ];
			max[ d ] -= offset[ d ];
		}
		return interval( offset( ( RandomAccessible< T > ) interval, offset ), min, max );
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
		final MixedTransform t = new MixedTransform( n, n );
		t.setTranslation( offset );
		return interval( new MixedTransformView< T >( interval, t ), min, max );
	}

	/**
	 * take a (n-1)-dimensional slice of a n-dimensional view, fixing
	 * d-component of coordinates to pos.
	 */
	public static < T > MixedTransformView< T > hyperSlice( final RandomAccessible< T > view, final int d, final long pos )
	{
		final int m = view.numDimensions();
		final int n = m - 1;
		final MixedTransform t = new MixedTransform( n, m );
		final long[] translation = new long[ m ];
		translation[ d ] = pos;
		final boolean[] zero = new boolean[ m ];
		final int[] component = new int[ m ];
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
	public static < T > IntervalView< T > hyperSlice( final RandomAccessibleInterval< T > view, final int d, final long pos )
	{
		final int m = view.numDimensions();
		final int n = m - 1;
		final long[] min = new long[ n ];
		final long[] max = new long[ n ];
		for ( int e = 0; e < m; ++e )
		{
			if ( e < d )
			{
				min[ e ] = view.min( e );
				max[ e ] = view.max( e );
			}
			else if ( e > d )
			{
				min[ e - 1 ] = view.min( e );
				max[ e - 1 ] = view.max( e );
			}
		}
		return interval( hyperSlice( ( RandomAccessible< T > ) view, d, pos ), min, max );
	}

	/**
	 * Create view which adds a dimension to the source {@link RandomAccessible}
	 * .
	 * 
	 * The additional dimension is the last dimension. For example, an XYZ view
	 * is created for an XY source. When accessing an XYZ sample in the view,
	 * the final coordinate is discarded and the source XY sample is accessed.
	 * 
	 * @param randomAccessible
	 *            the source
	 */
	public static < T > MixedTransformView< T > addDimension( final RandomAccessible< T > randomAccessible )
	{
		final int m = randomAccessible.numDimensions();
		final int n = m + 1;
		final MixedTransform t = new MixedTransform( n, m );
		return new MixedTransformView< T >( randomAccessible, t );
	}

	/**
	 * Create view which adds a dimension to the source
	 * {@link RandomAccessibleInterval}. The {@link Interval} boundaries in the
	 * additional dimension are set to the specified values.
	 * 
	 * The additional dimension is the last dimension. For example, an XYZ view
	 * is created for an XY source. When accessing an XYZ sample in the view,
	 * the final coordinate is discarded and the source XY sample is accessed.
	 * 
	 * @param interval
	 *            the source
	 * @param minOfNewDim
	 *            Interval min in the additional dimension.
	 * @param maxOfNewDim
	 *            Interval max in the additional dimension.
	 */
	public static < T > IntervalView< T > addDimension( final RandomAccessibleInterval< T > interval, final long minOfNewDim, final long maxOfNewDim )
	{
		final int m = interval.numDimensions();
		final long[] min = new long[ m + 1 ];
		final long[] max = new long[ m + 1 ];
		for ( int d = 0; d < m; ++d )
		{
			min[ d ] = interval.min( d );
			max[ d ] = interval.max( d );
		}
		min[ m ] = minOfNewDim;
		max[ m ] = maxOfNewDim;
		return interval( addDimension( interval ), min, max );
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
		final boolean[] inv = new boolean[ n ];
		inv[ d ] = true;
		final MixedTransform t = new MixedTransform( n, n );
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
		final long[] min = new long[ n ];
		final long[] max = new long[ n ];
		interval.min( min );
		interval.max( max );
		final long tmp = min[ d ];
		min[ d ] = -max[ d ];
		max[ d ] = -tmp;
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
	public static < T > IntervalView< T > offsetInterval( final RandomAccessible< T > randomAccessible, final long[] offset, final long[] dimension )
	{
		final int n = randomAccessible.numDimensions();
		final long[] min = new long[ n ];
		final long[] max = new long[ n ];
		for ( int d = 0; d < n; ++d )
			max[ d ] = dimension[ d ] - 1;
		return interval( offset( randomAccessible, offset ), min, max );
	}

	/**
	 * Define an interval on a RandomAccessible and translate it such that the
	 * min corner is at the origin. It is the callers responsibility to ensure
	 * that the source RandomAccessible is defined in the specified interval.
	 * 
	 * @param randomAccessible
	 *            the source
	 * @param interval
	 *            the interval on source that should be cut out and translated
	 *            to the origin.
	 * @return a RandomAccessibleInterval
	 */
	public static < T > IntervalView< T > offsetInterval( final RandomAccessible< T > randomAccessible, final Interval interval )
	{
		final int n = randomAccessible.numDimensions();
		final long[] offset = new long[ n ];
		final long[] min = new long[ n ];
		final long[] max = new long[ n ];
		interval.min( offset );
		interval.max( max );
		for ( int d = 0; d < n; ++d )
			max[ d ] -= offset[ d ];
		return interval( offset( randomAccessible, offset ), min, max );
	}

	/**
	 * test whether the source interval starts at (0,0,...,0)
	 * 
	 * @param interval
	 *            - the {@link Interval} to test
	 * @return true if zero-bounded, false otherwise
	 */
	public static boolean isZeroMin( final Interval interval )
	{
		for ( int d = 0; d < interval.numDimensions(); ++d )
			if ( interval.min( d ) != 0 )
				return false;

		return true;
	}

	/**
	 * Return an {@link IterableInterval}. If the passed
	 * {@link RandomAccessibleInterval} is already an {@link IterableInterval}
	 * then it is returned directly (this is the case for {@link Img}). If not,
	 * then an {@link IterableRandomAccessibleInterval} is created.
	 * 
	 * @param randomAccessibleInterval
	 *            the source
	 * @return an {@link IterableInterval}
	 */
	@SuppressWarnings( "unchecked" )
	public static < T > IterableInterval< T > iterable( final RandomAccessibleInterval< T > randomAccessibleInterval )
	{
		if ( IterableInterval.class.isInstance( randomAccessibleInterval ) )
			return ( IterableInterval< T > ) randomAccessibleInterval;
		return new IterableRandomAccessibleInterval< T >( randomAccessibleInterval );
	}

	/**
	 * Return an {@link IterableInterval} having {@link FlatIterationOrder}. If
	 * the passed {@link RandomAccessibleInterval} is already an
	 * {@link IterableInterval} with {@link FlatIterationOrder} then it is
	 * returned directly (this is the case for {@link ArrayImg}). If not, then
	 * an {@link IterableRandomAccessibleInterval} is created.
	 * 
	 * @param randomAccessibleInterval
	 *            the source
	 * @return an {@link IterableInterval} with {@link FlatIterationOrder}
	 */
	@SuppressWarnings( "unchecked" )
	public static < T > IterableInterval< T > flatIterable( final RandomAccessibleInterval< T > randomAccessibleInterval )
	{
		if ( IterableInterval.class.isInstance( randomAccessibleInterval ) && FlatIterationOrder.class.isInstance( ( ( IterableInterval< T > ) randomAccessibleInterval ).iterationOrder() ) )
			return ( IterableInterval< T > ) randomAccessibleInterval;
		return new IterableRandomAccessibleInterval< T >( randomAccessibleInterval );
	}

	/**
	 * Collapse the <em>n</em><sup>th</sup> dimension of an <em>n</em>
	 * -dimensional {@link RandomAccessibleInterval}&lt;T&gt; into an (
	 * <em>n</em>-1)-dimensional {@link RandomAccessibleInterval}&lt;
	 * {@link GenericComposite}&lt;T&gt;&gt;
	 * 
	 * @param source
	 *            the source
	 * @return an (<em>n</em>-1)-dimensional {@link CompositeIntervalView} of
	 *         {@link GenericComposite GenericComposites}
	 */
	public static < T > CompositeIntervalView< T, ? extends GenericComposite< T > > collapse( final RandomAccessibleInterval< T > source )
	{
		return new CompositeIntervalView< T, GenericComposite< T > >( source, new GenericComposite.Factory< T >() );
	}

	/**
	 * Collapse the <em>n</em><sup>th</sup> dimension of an <em>n</em>
	 * -dimensional {@link RandomAccessibleInterval}&lt;T extends
	 * {@link RealType}&lt;T&gt;&gt; into an (<em>n</em>-1)-dimensional
	 * {@link RandomAccessibleInterval}&lt;{@link RealComposite}&lt;T&gt;&gt;
	 * 
	 * @param source
	 *            the source
	 * @return an (<em>n</em>-1)-dimensional {@link CompositeIntervalView} of
	 *         {@link RealComposite RealComposites}
	 */
	public static < T extends RealType< T > > CompositeIntervalView< T, RealComposite< T > > collapseReal( final RandomAccessibleInterval< T > source )
	{
		return new CompositeIntervalView< T, RealComposite< T > >( source, new RealComposite.Factory< T >( ( int ) source.dimension( source.numDimensions() - 1 ) ) );
	}

	/**
	 * Collapse the <em>n</em><sup>th</sup> dimension of an <em>n</em>
	 * -dimensional {@link RandomAccessibleInterval}&lt;T extends
	 * {@link NumericType}&lt;T&gt;&gt; into an (<em>n</em>-1)-dimensional
	 * {@link RandomAccessibleInterval}&lt;{@link NumericComposite}&lt;T&gt;&gt;
	 * 
	 * @param source
	 *            the source
	 * @return an (<em>n</em>-1)-dimensional {@link CompositeIntervalView} of
	 *         {@link NumericComposite NumericComposites}
	 */
	public static < T extends NumericType< T > > CompositeIntervalView< T, NumericComposite< T > > collapseNumeric( final RandomAccessibleInterval< T > source )
	{
		return new CompositeIntervalView< T, NumericComposite< T > >( source, new NumericComposite.Factory< T >( ( int ) source.dimension( source.numDimensions() - 1 ) ) );
	}

	/**
	 * Collapse the <em>n</em><sup>th</sup> dimension of an <em>n</em>
	 * -dimensional {@link RandomAccessible}&lt;T&gt; into an (<em>n</em>
	 * -1)-dimensional {@link RandomAccessible}&lt;{@link GenericComposite}
	 * &lt;T&gt;&gt;
	 * 
	 * @param source
	 *            the source
	 * @return an (<em>n</em>-1)-dimensional {@link CompositeView} of
	 *         {@link GenericComposite GenericComposites}
	 */
	public static < T > CompositeView< T, ? extends GenericComposite< T > > collapse( final RandomAccessible< T > source )
	{
		return new CompositeView< T, GenericComposite< T > >( source, new GenericComposite.Factory< T >() );
	}

	/**
	 * Collapse the <em>n</em><sup>th</sup> dimension of an <em>n</em>
	 * -dimensional {@link RandomAccessible}&lt;T extends {@link RealType}
	 * &lt;T&gt;&gt; into an (<em>n</em>-1)-dimensional {@link RandomAccessible}
	 * &lt;{@link RealComposite}&lt;T&gt;&gt;
	 * 
	 * @param source
	 *            the source
	 * @param numChannels
	 *            the number of channels that the {@link RealComposite} will
	 *            consider when performing calculations
	 * @return an (<em>n</em>-1)-dimensional {@link CompositeView} of
	 *         {@link RealComposite RealComposites}
	 */
	public static < T extends RealType< T > > CompositeView< T, RealComposite< T > > collapseReal( final RandomAccessible< T > source, final int numChannels )
	{
		return new CompositeView< T, RealComposite< T > >( source, new RealComposite.Factory< T >( numChannels ) );
	}

	/**
	 * Collapse the <em>n</em><sup>th</sup> dimension of an <em>n</em>
	 * -dimensional {@link RandomAccessible}&lt;T extends {@link NumericType}
	 * &lt;T&gt;&gt; into an (<em>n</em>-1)-dimensional {@link RandomAccessible}
	 * &lt;{@link NumericComposite}&lt;T&gt;&gt;
	 * 
	 * @param source
	 *            the source
	 * @param numChannels
	 *            the number of channels that the {@link NumericComposite} will
	 *            consider when performing calculations
	 * @return an (<em>n</em>-1)-dimensional {@link CompositeView} of
	 *         {@link NumericComposite NumericComposites}
	 */
	public static < T extends NumericType< T > > CompositeView< T, NumericComposite< T > > collapseNumeric( final RandomAccessible< T > source, final int numChannels )
	{
		return new CompositeView< T, NumericComposite< T > >( source, new NumericComposite.Factory< T >( numChannels ) );
	}

	/**
	 * Sample only every <em>step</em><sup>th</sup> value of a source
	 * {@link RandomAccessibleInterval}. This is effectively an integer scaling
	 * and zero offset transformation.
	 * 
	 * @param source
	 *            the source
	 * @param step
	 *            the subsampling step size
	 * @return a subsampled {@link RandomAccessibleInterval} with its origin
	 *         coordinates at zero
	 */
	public static < T > SubsampleIntervalView< T > subsample( final RandomAccessibleInterval< T > source, final long step )
	{
		return new SubsampleIntervalView< T >( source, step );
	}

	/**
	 * Sample only every <em>step<sub>d</sub></em><sup>th</sup> value of a
	 * source {@link RandomAccessibleInterval}. This is effectively an integer
	 * scaling and zero offset transformation.
	 * 
	 * @param source
	 *            the source
	 * @param steps
	 *            the subsampling step sizes
	 * @return a subsampled {@link RandomAccessibleInterval} with its origin
	 *         coordinates at zero
	 */
	public static < T > SubsampleView< T > subsample( final RandomAccessibleInterval< T > source, final long... steps )
	{
		assert steps.length >= source.numDimensions(): "Dimensions do not match.";

		return new SubsampleIntervalView< T >( source, steps );
	}

	/**
	 * Sample only every <em>step</em><sup>th</sup> value of a source
	 * {@link RandomAccessible}. This is effectively an integer scaling
	 * transformation.
	 * 
	 * @param source
	 *            the source
	 * @param step
	 *            the subsampling step size
	 * @return a subsampled {@link RandomAccessible}
	 * 
	 */
	public static < T > SubsampleView< T > subsample( final RandomAccessible< T > source, final long step )
	{
		return new SubsampleView< T >( source, step );
	}

	/**
	 * Sample only every <em>step<sub>d</sub></em><sup>th</sup> value of a
	 * source {@link RandomAccessible}. This is effectively an integer scaling
	 * transformation.
	 * 
	 * @param source
	 *            the source
	 * @param steps
	 *            the subsampling step sizes
	 * @return a subsampled {@link RandomAccessible}
	 * 
	 */
	public static < T > SubsampleView< T > subsample( final RandomAccessible< T > source, final long... steps )
	{
		assert steps.length >= source.numDimensions(): "Dimensions do not match.";

		return new SubsampleView< T >( source, steps );
	}
}
