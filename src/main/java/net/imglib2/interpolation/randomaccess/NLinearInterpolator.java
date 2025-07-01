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

import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RealRandomAccess;
import net.imglib2.position.transform.Floor;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.util.IntervalIndexer;

/**
 * Performs multi-linear interpolation.
 *
 * @param <T>
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Tobias Pietzsch
 */
public class NLinearInterpolator< T extends NumericType< T > > extends AbstractNLinearInterpolator< T >
{
	/**
	 * Index into {@link #weights} array.
	 *
	 * <p>
	 * To visit the pixels that contribute to an interpolated value, we move in
	 * a (binary-reflected) Gray code pattern, such that only one dimension of
	 * the target position is modified per move.
	 * </p>
	 * <p>
	 * This index is the corresponding gray code bit pattern which will select
	 * the correct corresponding weight.
	 * </p>
	 * <p>
	 * See
	 * <a href="http://en.wikipedia.org/wiki/Gray_code">http://en.wikipedia.org/
	 * wiki/Gray_code</a>
	 * </p>
	 */
	private int code;

	/**
	 * Weights for each pixel of the <em>2x2x...x2</em> hypercube of pixels
	 * participating in the interpolation.
	 *
	 * <p>
	 * Indices into this array are arranged in the standard iteration order (as
	 * provided by {@link IntervalIndexer#positionToIndex}). Element 0 refers to
	 * position <em>(0,0,...,0)</em>, element 1 refers to position
	 * <em>(1,0,...,0)</em>, element 2 refers to position <em>(0,1,...,0)</em>,
	 * etc.
	 * </p>
	 */
	protected final T accumulator;

	protected final T tmp;

	protected NLinearInterpolator( final NLinearInterpolator< T > interpolator )
	{
		super( interpolator );

		accumulator = type.createVariable();
		tmp = type.createVariable();
	}

	protected NLinearInterpolator( final RandomAccessible< T > randomAccessible, final T type )
	{
		super( randomAccessible, type );

		accumulator = type.createVariable();
		tmp = type.createVariable();
	}

	protected NLinearInterpolator( final RandomAccessible< T > randomAccessible )
	{
		this( randomAccessible, randomAccessible.getType() );
	}

	@Override
	public T get()
	{
		fillWeights();

		accumulator.set( target.get() );
		accumulator.mul( weights[ 0 ] );

		code = 0;
		graycodeFwdRecursive( n - 1 );
		target.bck( n - 1 );

		return accumulator;
	}

	@Override
	public NLinearInterpolator< T > copy()
	{
		return new NLinearInterpolator<>( this );
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
		tmp.set( target.get() );
		tmp.mul( weights[ code ] );
		accumulator.add( tmp );
	}
}
