/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2020 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RealRandomAccess;
import net.imglib2.Sampler;
import net.imglib2.position.transform.FloorOffset;
import net.imglib2.type.numeric.RealType;

/**
 * n-dimensional double-based Lanczos Interpolation
 * 
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class LanczosInterpolator< T extends RealType< T > > extends FloorOffset< RandomAccess< T > > implements RealRandomAccess< T >
{
	final static protected double piSquare = Math.PI * Math.PI;

	final static protected int lutScale = 10;

	final protected int alpha;

	final protected T interpolatedValue;

	final protected long[] size, max;

	final protected double minValue, maxValue;

	final protected boolean clip;

	final protected double[] lut, products;

	final static private long[] createOffset( final int a, final int n )
	{
		final long[] offset = new long[ n ];
		for ( int d = 0; d < n; ++d )
			offset[ d ] = -a + 1;
		return offset;
	}

	/**
	 * Creates a new Lanczos-interpolation
	 * 
	 * @param randomAccessible
	 *            - the {@link RandomAccessible} to work on
	 * @param alpha
	 *            - the radius of values to incorporate (typically 2 or 3)
	 * @param clip
	 *            - clips the value to range of the {@link RealType}, i.e. tests
	 *            if the interpolated value is out of range
	 * @param min
	 *            - range for clipping (ignored if min==max)
	 * @param max
	 *            - range for clipping (ignored if min==max)
	 */
	public LanczosInterpolator( final RandomAccessible< T > randomAccessible, final int alpha, final boolean clip, final double min, final double max )
	{
		super( randomAccessible.randomAccess(), createOffset( alpha, randomAccessible.numDimensions() ) );

		this.alpha = alpha;

		lut = createLanczosLUT( alpha, lutScale );
		products = new double[ n + 1 ];
		products[ n ] = 1.0;

		this.size = new long[ n ];
		this.max = new long[ n ];

		for ( int d = 0; d < n; ++d )
			size[ d ] = alpha * 2;

		this.clip = clip;

		this.interpolatedValue = target.get().createVariable();

		if ( min == max )
		{
			this.minValue = interpolatedValue.getMinValue();
			this.maxValue = interpolatedValue.getMaxValue();
		}
		else
		{
			this.minValue = min;
			this.maxValue = max;
		}
	}

	public LanczosInterpolator( final LanczosInterpolator< T > interpolator )
	{
		super( interpolator.target.copyRandomAccess(), interpolator.offset );

		this.alpha = interpolator.alpha;

		lut = interpolator.lut.clone();
		products = interpolator.products.clone();

		this.size = interpolator.size.clone();
		this.max = interpolator.max.clone();

		this.clip = interpolator.clip;

		this.interpolatedValue = interpolator.interpolatedValue.copy();
		this.minValue = interpolator.minValue;
		this.maxValue = interpolator.maxValue;
	}

	final static private double[] createLanczosLUT( final int max, final int scale )
	{
		final double[] lut = new double[ max * scale + 2 ];
		for ( int i = 0; i < lut.length; ++i )
		{
			final double x = ( double ) i / ( double ) lutScale;
			lut[ i ] = lanczos( x, max );
		}
		return lut;
	}

	final protected void resetKernel()
	{
		for ( int d = n - 1; d >= 0; --d )
		{
			final long p = target.getLongPosition( d );
			max[ d ] = p + size[ d ];
			products[ d ] = lookUpLanczos( position[ d ] - p ) * products[ d + 1 ];
		}
	}

	final protected void accumulate( final int d )
	{
		for ( int e = d; e >= 0; --e )
			products[ e ] = lookUpLanczos( position[ e ] - target.getLongPosition( e ) ) * products[ e + 1 ];
	}

	@Override
	public T get()
	{
		double convolved = 0;

		resetKernel();

		boolean proceed = true;

		A: while ( proceed )
		{
			convolved += target.get().getRealDouble() * products[ 0 ];

			for ( int d = 0; d < n; ++d )
			{
				target.fwd( d );
				final long p = target.getLongPosition( d );
				if ( p < max[ d ] )
				{
					products[ d ] = lookUpLanczos( position[ d ] - p ) * products[ ++d ];
					continue A;
				}
				target.move( -size[ d ], d );
				accumulate( d );
			}
			proceed = false;
		}

		// do clipping if desired (it should be, except maybe for float or
		// double input)
		if ( clip )
		{
			if ( convolved < minValue )
				convolved = minValue;
			else if ( convolved > maxValue )
				convolved = maxValue;
		}

		interpolatedValue.setReal( convolved );

		return interpolatedValue;
	}

	private static final double lanczos( final double x, final double a )
	{
		if ( x == 0 )
			return 1;
		return ( ( a * Math.sin( Math.PI * x ) * Math.sin( Math.PI * x / a ) ) / ( piSquare * x * x ) );
	}

	final private double lookUpLanczos( final double x )
	{
		final double y = x < 0 ? -lutScale * x : lutScale * x;
		final int yi = ( int ) y;
		final double d = y - yi;
		return ( lut[ yi + 1 ] - lut[ yi ] ) * d + lut[ yi ];
	}

	@Override
	public Sampler< T > copy()
	{
		return copy();
	}

	@Override
	public RealRandomAccess< T > copyRealRandomAccess()
	{
		return new LanczosInterpolator< T >( this );
	}
}
