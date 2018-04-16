/*
 * #%L
 * BigDataViewer core classes with minimal dependencies
 * %%
 * Copyright (C) 2012 - 2016 Tobias Pietzsch, Stephan Saalfeld, Stephan Preibisch,
 * Jean-Yves Tinevez, HongKee Moon, Johannes Schindelin, Curtis Rueden, John Bogovic
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
import net.imglib2.type.numeric.RealType;

/**
 * N-linear interpolator for {@link RealType} values with overflow check.
 * Interpoalted values are clamped to the range {@link RealType#getMinValue()},{@link RealType#getMaxValue()}.
 *
 * @param <T>
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public class ClampingNLinearInterpolatorRealType< T extends RealType< T > > extends NLinearInterpolator< T >
{
	protected double acc;
	protected final double clampMin;
	protected final double clampMax;

	protected ClampingNLinearInterpolatorRealType( final ClampingNLinearInterpolatorRealType< T > interpolator )
	{
		super( interpolator );
		clampMin = interpolator.clampMin;
		clampMax = interpolator.clampMax;
	}

	protected ClampingNLinearInterpolatorRealType( final RandomAccessible< T > randomAccessible, final T type )
	{
		super( randomAccessible, type );
		clampMin = type.getMinValue();
		clampMax = type.getMaxValue();
	}

	protected ClampingNLinearInterpolatorRealType( final RandomAccessible< T > randomAccessible )
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
		acc = target.get().getRealDouble() * weights[ 0 ];
		code = 0;
		graycodeFwdRecursive( n - 1 );
		target.bck( n - 1 );
		accumulator.setReal( Math.max( clampMin, Math.min( clampMax, acc ) ) );
		return accumulator;
	}

	@Override
	public ClampingNLinearInterpolatorRealType< T > copy()
	{
		return new ClampingNLinearInterpolatorRealType<>( this );
	}

	@Override
	public ClampingNLinearInterpolatorRealType< T > copyRealRandomAccess()
	{
		return copy();
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
		acc += target.get().getRealDouble() * weights[ code ];
	}
}
