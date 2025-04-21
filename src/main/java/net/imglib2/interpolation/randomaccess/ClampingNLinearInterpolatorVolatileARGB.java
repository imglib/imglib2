/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2025 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import net.imglib2.RandomAccessible;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.volatiles.AbstractVolatileNumericType;
import net.imglib2.util.Util;

/**
 * N-linear interpolator for volatile ARGB values with overflow check.
 *
 * @param <T>
 *
 * @author Stephan Saalfeld &lt;saalfeld@mpi-cbg.de&gt;
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public class ClampingNLinearInterpolatorVolatileARGB< T extends AbstractVolatileNumericType< ARGBType, T > > extends NLinearInterpolator< T >
{
	protected double accA, accR, accG, accB;
	protected boolean valid;

	protected ClampingNLinearInterpolatorVolatileARGB( final ClampingNLinearInterpolatorVolatileARGB< T > interpolator )
	{
		super( interpolator );
	}

	protected ClampingNLinearInterpolatorVolatileARGB( final RandomAccessible< T > randomAccessible, final T type )
	{
		super( randomAccessible, type );
	}

	protected ClampingNLinearInterpolatorVolatileARGB( final RandomAccessible< T > randomAccessible )
	{
		this( randomAccessible, randomAccessible.randomAccess().get() );
	}

	/**
	 * Get the interpolated value at the current position.
	 *
	 * <p>
	 * To visit the pixels that contribute to an interpolated value, we move in
	 * a (binary-reflected) Gray code pattern, such that only one dimension of
	 * the target position is modified per move.
	 *
	 * <p>
	 * @see <a href="http://en.wikipedia.org/wiki/Gray_code">Gray code</a>.
	 */
	@Override
	public T get()
	{
		fillWeights();

		final T t = target.get();
		final int argb = t.get().get();
		valid = t.isValid();
		accA = ( ( argb >> 24 ) & 0xff ) * weights[ 0 ];
		accR = ( ( argb >> 16 ) & 0xff ) * weights[ 0 ];
		accG = ( ( argb >> 8 ) & 0xff ) * weights[ 0 ];
		accB = ( argb & 0xff ) * weights[ 0 ];

		code = 0;
		graycodeFwdRecursive( n - 1 );
		target.bck( n - 1 );

		final int a = Math.min( 255, ( int ) Util.round( accA ) );
		final int r = Math.min( 255, ( int ) Util.round( accR ) );
		final int g = Math.min( 255, ( int ) Util.round( accG ) );
		final int b = Math.min( 255, ( int ) Util.round( accB ) );

		accumulator.get().set( ( ( ( ( ( a << 8 ) | r ) << 8 ) | g ) << 8 ) | b );
		accumulator.setValid( valid );

		return accumulator;
	}

	@Override
	public ClampingNLinearInterpolatorVolatileARGB< T > copy()
	{
		return new ClampingNLinearInterpolatorVolatileARGB<>( this );
	}

	final private void graycodeFwdRecursive( final int dimension )
	{
		if ( dimension == 0 )
		{
			target.fwd( 0 );
			code += 1;
			accumulate();
		}
		else
		{
			graycodeFwdRecursive( dimension - 1 );
			target.fwd( dimension );
			code += 1 << dimension;
			accumulate();
			graycodeBckRecursive( dimension - 1 );
		}
	}

	final private void graycodeBckRecursive( final int dimension )
	{
		if ( dimension == 0 )
		{
			target.bck( 0 );
			code -= 1;
			accumulate();
		}
		else
		{
			graycodeFwdRecursive( dimension - 1 );
			target.bck( dimension );
			code -= 1 << dimension;
			accumulate();
			graycodeBckRecursive( dimension - 1 );
		}
	}

	/**
	 * multiply current target value with current weight and add to accumulator.
	 */
	final private void accumulate()
	{
		final T t = target.get();
		final int argb = t.get().get();
		valid &= t.isValid();
		accA += ( ( argb >> 24 ) & 0xff ) * weights[ code ];
		accR += ( ( argb >> 16 ) & 0xff ) * weights[ code ];
		accG += ( ( argb >> 8 ) & 0xff ) * weights[ code ];
		accB += ( argb & 0xff ) * weights[ code ];
	}
}
