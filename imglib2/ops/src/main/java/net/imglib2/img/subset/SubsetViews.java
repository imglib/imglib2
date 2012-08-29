/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package net.imglib2.img.subset;

import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.transform.integer.MixedTransform;
import net.imglib2.type.Type;
import net.imglib2.util.Util;
import net.imglib2.view.IntervalView;
import net.imglib2.view.IterableRandomAccessibleInterval;
import net.imglib2.view.MixedTransformView;
import net.imglib2.view.Views;

/**
 * @author dietzc, hornm (University of Konstanz)
 * 
 */
public class SubsetViews
{

	/**
	 * See SubsetViews.subsetView(...) Difference: If possible an optimized
	 * {@link Cursor} will be created.
	 * 
	 * 
	 * @param <T>
	 * @param src
	 *            Source {@link RandomAccessibleInterval}
	 * @param interval
	 *            Interval defining dimensionality of resulting
	 *            {@link IterableRandomAccessibleInterval}
	 * @return
	 */
	public static final < T extends Type< T >> IterableRandomAccessibleInterval< T > iterableSubsetView( final RandomAccessibleInterval< T > src, final Interval interval )
	{
		return new IterableSubsetView< T >( src, interval );
	}

	/**
	 * Permutates RandomAccessible
	 * 
	 * @param <T>
	 * @param randomAccessible
	 * @param fromAxis
	 * @param toAxis
	 * @return
	 */
	public static < T > MixedTransformView< T > permutate( final RandomAccessible< T > randomAccessible, final int fromAxis, final int toAxis )
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
	 * View on interval of a source. If wanted, dims with size 1 are removed.
	 * 
	 * @param src
	 *            The source {@link RandomAccessibleInterval}
	 * @param interval
	 *            Interval
	 * @param keepDimsWithSizeOne
	 *            If false, dimensions with size one will be virtually removed
	 *            from the resulting view
	 * @return
	 */
	public static final < T extends Type< T >> RandomAccessibleInterval< T > subsetView( final RandomAccessibleInterval< T > src, final Interval interval )
	{

		boolean oneSizedDims = false;

		for ( int d = 0; d < interval.numDimensions(); d++ )
		{
			if ( interval.dimension( d ) == 1 )
			{
				oneSizedDims = true;
				break;
			}
		}

		if ( intervalEquals( src, interval ) && !oneSizedDims )
			return src;

		RandomAccessibleInterval< T > res;
		if ( Util.contains( src, interval ) )
			res = Views.offsetInterval( src, interval );
		else
			throw new IllegalArgumentException( "Interval must fit into src in SubsetViews.subsetView(...)" );

		for ( int d = interval.numDimensions() - 1; d >= 0; --d )
			if ( interval.dimension( d ) == 1 && res.numDimensions() > 1 )
				res = Views.hyperSlice( res, d, 0 );

		return res;
	}

	/**
	 * Adds an dimension to the end of a {@link RandomAccessible}
	 * 
	 * @param <T>
	 * @param view
	 * @return
	 */
	public static < T > MixedTransformView< T > addDimension( final RandomAccessible< T > view )
	{
		final int m = view.numDimensions();
		final int n = m + 1;
		final MixedTransform t = new MixedTransform( n, m );
		return new MixedTransformView< T >( view, t );
	}

	/**
	 * Adds Dimension with given min and max to end of
	 * {@link RandomAccessibleInterval}
	 * 
	 * @param <T>
	 * @param view
	 * @return
	 */
	public static < T > IntervalView< T > addDimension( final RandomAccessibleInterval< T > view, final long minOfNewDim, final long maxOfNewDim )
	{
		final int m = view.numDimensions();
		final long[] min = new long[ m + 1 ];
		final long[] max = new long[ m + 1 ];
		for ( int d = 0; d < m; ++d )
		{
			min[ d ] = view.min( d );
			max[ d ] = view.max( d );
		}
		min[ m ] = minOfNewDim;
		max[ m ] = maxOfNewDim;
		return Views.interval( addDimension( view ), min, max );
	}

	/**
	 * {@link RandomAccessibleInterval} with same sice as target is returned
	 * 
	 * @param src
	 *            {@link RandomAccessibleInterval} to be adjusted
	 * @param target
	 *            {@link Interval} describing the resulting sizes
	 * @return Adjusted {@link RandomAccessibleInterval}
	 */
	public static < T > RandomAccessibleInterval< T > synchronizeDimensionality( final RandomAccessibleInterval< T > src, final Interval target )
	{
		RandomAccessibleInterval< T > res = src;

		// Check direction of conversion
		if ( intervalEquals( src, target ) )
			return res;

		// adjust dimensions
		if ( res.numDimensions() < target.numDimensions() )
		{
			for ( int d = res.numDimensions(); d < target.numDimensions(); d++ )
			{
				res = addDimension( res, target.min( d ), target.max( d ) );
			}
		}
		else
		{
			for ( int d = res.numDimensions() - 1; d >= target.numDimensions(); --d )
				res = Views.hyperSlice( res, d, 0 );
		}

		long[] resDims = new long[ res.numDimensions() ];
		res.dimensions( resDims );

		return Views.interval( Views.extendBorder( res ), target );

	}

	/**
	 * Checks weather to intervals have same dimensionality
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static synchronized boolean intervalEquals( Interval a, Interval b )
	{

		if ( a.numDimensions() != b.numDimensions() ) { return false; }

		for ( int d = 0; d < a.numDimensions(); d++ )
		{
			if ( a.min( d ) != b.min( d ) || a.max( d ) != b.max( d ) )
				return false;
		}

		return true;
	}
}
