/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2018 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
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

package net.imglib2.interpolation.randomaccess;

import java.util.Arrays;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealRandomAccess;
import net.imglib2.bspline.BSplineDecomposition;
import net.imglib2.neighborhood.Neighborhood;
import net.imglib2.neighborhood.RectangleShape;
import net.imglib2.position.transform.Floor;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Util;
import net.imglib2.view.Views;

/**
 * Performs cubic b-spline interpolation by computing coefficients on the fly.
 * This will be less time efficient, in general, than pre-computing coefficients
 * using a {@link BSplineDecomposition}. This will be more memory-efficient
 * though.
 *
 * See Unser, Aldroubi, and Eden "Fast B-Spline Transforms for Continuous Image
 * Representation and Interpolation" IEEE PAMI 13(3) 1991.
 *
 * @param <T>
 *            the image type
 *
 * @author John Bogovic
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Tobias Pietzsch
 */
public class BSplineInterpolator< T extends RealType< T > > extends Floor< RandomAccess< Neighborhood< T > > > implements RealRandomAccess< T >
{
	private static final long serialVersionUID = 7201790873893099953L;

	public static final double SQRT3 = Math.sqrt( 3.0 );

	// from Unser 1991 Table 2
	public static final double ALPHA = SQRT3 - 2.0;
	public static final double FACTOR = ( -6.0 * ALPHA ) / ( 1.0 - ( ALPHA * ALPHA ) ); // TODO where did that come from?
	public static final double ONESIXTH = 1.0 / 6.0;
	public static final double TWOTHIRDS = 2.0 / 3.0;
	public static final double FOURTHIRDS = 4.0 / 3.0;

	protected double w;

	final protected T value;

	final protected double[][] weights;

	final protected boolean clipping;

	final protected int bsplineOrder;

	final protected int radius;

	protected final RectangleShape shape;

	private static final int[] arrayOf( final int i, final int n )
	{

		final int[] array = new int[ n ];
		Arrays.fill( array, i );
		return array;

	}

	public BSplineInterpolator( final BSplineInterpolator< T > interpolator )
	{
		super( interpolator.target.copyRandomAccess() );

		this.shape = interpolator.shape;

		this.bsplineOrder = interpolator.bsplineOrder;
		this.radius = bsplineOrder + 1;
		this.clipping = interpolator.clipping;
		value = target.setPositionAndGet( new int[ numDimensions() ] ).firstElement().createVariable();
		weights = new double[ numDimensions() ][ shape.getSpan() * 2 + 1 ];
	}

	private BSplineInterpolator( final RandomAccessible< T > source, final int order, final RectangleShape shape, final boolean clipping )
	{
		super( shape.neighborhoodsRandomAccessible( source ).randomAccess() );

		this.shape = shape;

		this.bsplineOrder = order;
		this.radius = bsplineOrder + 1;
		this.clipping = clipping;
		value = target.setPositionAndGet( new int[ numDimensions() ] ).firstElement().createVariable();
		weights = new double[ numDimensions() ][ shape.getSpan() * 2 + 1 ];
	}

	public BSplineInterpolator( final RandomAccessible< T > source, final int order, final int radius, final boolean clipping )
	{
		this( source, order, new RectangleShape( radius, false ), clipping );
	}

	/**
	 * Build a default cubic-bspline interpolator.
	 *
	 * @param randomAccessible
	 *            the random accessible
	 */
	protected BSplineInterpolator( final RandomAccessibleInterval< T > randomAccessible )
	{
		this( randomAccessible, 3, 4, true );
	}

	public void printPosition()
	{
		System.out.println( "interp position : " + Arrays.toString( position ) );
		System.out.println( "target position : " + Util.printCoordinates( target ) );
	}

	@Override
	public T get()
	{
		fillWeights();

		double accumulator = 0;

		final Cursor< T > c = target.get().cursor();

		while ( c.hasNext() )
		{
			double tmp = c.next().getRealDouble();
			for ( int d = 0; d < numDimensions(); d++ )
			{
				final int index = ( int ) ( c.getLongPosition( d ) - target.getLongPosition( d ) + shape.getSpan() );
				tmp *= weights[ d ][ index ];
			}
			accumulator += tmp;
		}

		if ( clipping )
			value.setReal( Math.min( value.getMaxValue(), Math.max( value.getMinValue(), accumulator ) ) );
		else
			value.setReal( accumulator );

		return value;
	}

	protected void fillWeights()
	{
		final Neighborhood< T > rect = target.get();
		for ( int d = 0; d < numDimensions(); d++ )
		{
			final double pos = position[ d ];
			final long min = rect.min( d );
			final long max = rect.max( d );
			for ( long i = min; i <= max; ++i )
				weights[ d ][ ( int ) ( i - min ) ] = cubicCardinalSpline( pos - i, radius );
		}
	}

	public < T extends RealType< T > > void printValues( final RandomAccessibleInterval< T > vals )
	{
		System.out.println( "\nvalues: " );
		final Cursor< T > c = Views.flatIterable( vals ).cursor();
		int yp = -1;
		while ( c.hasNext() )
		{
			final T v = c.next();
			String prefix = "  ";
			if ( yp != -1 && c.getIntPosition( 1 ) != yp )
				prefix = "\n  ";

			yp = c.getIntPosition( 1 );
			System.out.print( prefix + v );

		}
		System.out.print( "\n" );
	}

	@Override
	public BSplineInterpolator< T > copy()
	{
		return new BSplineInterpolator< T >( this );
	}

	@Override
	public BSplineInterpolator< T > copyRealRandomAccess()
	{
		return copy();
	}

	/*
	 * Third order spline kernel
	 */
	public static double evaluate3Normalized( final double u )
	{
		final double absValue = Math.abs( u );
		final double sqrValue = u * u;
		if ( absValue <= 1.0 )
			return ( TWOTHIRDS - sqrValue + 0.5 * sqrValue * absValue );
		else if ( absValue < 2.0 )
		{
			final double twoMinusAbsValue = 2 - absValue;
			return twoMinusAbsValue * twoMinusAbsValue * twoMinusAbsValue * ONESIXTH;
		}
		else
			return 0.0;
	}

	private static double powIntPositive( final double base, final int pow )
	{
		double result = 1;
		for ( int i = 0; i < pow; ++i )
		{
			result *= base;
		}
		return result;
	}

	/*
	 * Unser 1991 equations 3.19 and 3.20
	 */
	public static double cubicCardinalSpline( final double x, final int width )
	{
		double result = 0.0;
		for ( int k = -width; k <= width; k++ )
		{
			result += powIntPositive( ALPHA, Math.abs( k ) ) * evaluate3Normalized( x - k );
		}
		result *= FACTOR;

		return result;
	}

	final private void printWeights()
	{
		for ( int i = 0; i < weights.length; ++i )
			System.out.printf( "weights [ %2d ] = %f\n", i, weights[ i ] );
	}

}
