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

package net.imglib2.type.mask.interpolation;

import net.imglib2.RandomAccessible;
import net.imglib2.interpolation.randomaccess.AbstractNLinearInterpolator;
import net.imglib2.type.Type;
import net.imglib2.type.mask.Masked;
import net.imglib2.type.numeric.NumericType;

/**
 * N-linear interpolator for masked {@link NumericType} values.
 * <p>
 * Multiplication and addition operations in the interpolator are implemented
 * for pre-multiplied alpha, i.e. {@code (v, a)*s := (v, a*s)} and
 * {@code (v, a) + (w, b) := ((v*a+w*b)/(a+b), a+b)}.
 *
 * @param <N>
 * 		the {@code RealType} that is wrapped by the masked type {@code T}
 * @param <T>
 * 		the masked type
 *
 * @author Tobias Pietzsch
 */
public class MaskedNLinearInterpolator< N extends NumericType< N >, T extends Masked< N > & Type< T > > extends AbstractNLinearInterpolator< T >
{
	private int code;
	private double accAlpha;
	private final N tmp;

	protected MaskedNLinearInterpolator( final MaskedNLinearInterpolator< N, T > interpolator )
	{
		super( interpolator );
		tmp = type.value().createVariable();
	}

	protected MaskedNLinearInterpolator( final RandomAccessible< T > randomAccessible, final T type )
	{
		super( randomAccessible, type );
		tmp = type.value().createVariable();
	}

	protected MaskedNLinearInterpolator( final RandomAccessible< T > randomAccessible )
	{
		this( randomAccessible, randomAccessible.getType() );
	}

	@Override
	public T get()
	{
		fillWeights();
		final T t = target.get();
		final double walpha = weights[ 0 ] * t.mask();
		accAlpha = walpha;
		accumulator.value().set( t.value() );
		accumulator.value().mul( walpha );
		code = 0;
		graycodeFwdRecursive( n - 1 );
		target.bck( n - 1 );
		accumulator.value().mul( accAlpha < EPSILON ? 0 : ( 1.0 / accAlpha ) );
		accumulator.setMask(accAlpha);
		return accumulator;
	}

	private static final double EPSILON = 1e-10;

	@Override
	public MaskedNLinearInterpolator< N, T > copy()
	{
		return new MaskedNLinearInterpolator<>( this );
	}

	private void graycodeFwdRecursive( final int dimension )
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

	private void graycodeBckRecursive( final int dimension )
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
	private void accumulate()
	{
		final T t = target.get();
		final double walpha = weights[ code ] * t.mask();
		accAlpha += walpha;
		tmp.set( t.value() );
		tmp.mul( walpha );
		accumulator.value().add( tmp );
	}
}
