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
public abstract class AbstractNLinearInterpolator< T > extends Floor< RandomAccess< T > > implements RealRandomAccess< T >
{
	/*
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
//	int code;

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
	protected final double[] weights;

	protected final T type;

	protected AbstractNLinearInterpolator( final AbstractNLinearInterpolator< T > interpolator )
	{
		super( interpolator.target.copy() );

		weights = interpolator.weights.clone();
		type = interpolator.type;

		for ( int d = 0; d < n; ++d )
		{
			position[ d ] = interpolator.position[ d ];
			discrete[ d ] = interpolator.discrete[ d ];
		}
	}

	protected AbstractNLinearInterpolator( final RandomAccessible< T > randomAccessible, final T type )
	{
		super( randomAccessible.randomAccess() );

		weights = new double[ 1 << n ];
		this.type = type;
	}

	/**
	 * Fill the {@link #weights} array.
	 *
	 * <p>
	 * Let <em>w_d</em> denote the fraction of a pixel at which the sample
	 * position <em>p_d</em> lies from the floored position <em>pf_d</em> in
	 * dimension <em>d</em>. That is, the value at <em>pf_d</em> contributes
	 * with <em>(1 - w_d)</em> to the sampled value; the value at
	 * <em>( pf_d + 1 )</em> contributes with <em>w_d</em>.
	 * </p>
	 * <p>
	 * At every pixel, the total weight results from multiplying the weights of
	 * all dimensions for that pixel. That is, the "top-left" contributing pixel
	 * (position floored in all dimensions) gets assigned weight
	 * <em>(1-w_0)(1-w_1)...(1-w_n)</em>.
	 * </p>
	 * <p>
	 * We work through the weights array starting from the highest dimension.
	 * For the highest dimension, the first half of the weights contain the
	 * factor <em>(1 - w_n)</em> because this first half corresponds to floored
	 * pixel positions in the highest dimension. The second half contain the
	 * factor <em>w_n</em>. In this first step, the first weight of the first
	 * half gets assigned <em>(1 - w_n)</em>. The first element of the second
	 * half gets assigned <em>w_n</em>
	 * </p>
	 * <p>
	 * From their, we work recursively down to dimension 0. That is, each half
	 * of weights is again split recursively into two partitions. The first
	 * element of the second partitions is the first element of the half
	 * multiplied with <em>(w_d)</em>. The first element of the first partitions
	 * is multiplied with <em>(1 - w_d)</em>.
	 * </p>
	 * <p>
	 * When we have reached dimension 0, all weights will have a value assigned.
	 * </p>
	 */
	protected void fillWeights()
	{
		weights[ 0 ] = 1.0d;

		for ( int d = n - 1; d >= 0; --d )
		{
			final double w = position[ d ] - target.getLongPosition( d );
			final double wInv = 1.0d - w;
			final int wInvIndexIncrement = 1 << d;
			final int loopCount = 1 << ( n - 1 - d );
			final int baseIndexIncrement = wInvIndexIncrement * 2;
			int baseIndex = 0;
			for ( int i = 0; i < loopCount; ++i )
			{
				weights[ baseIndex + wInvIndexIncrement ] = weights[ baseIndex ] * w;
				weights[ baseIndex ] *= wInv;
				baseIndex += baseIndexIncrement;
			}
		}
	}

	/**
	 * Get the interpolated value at the current position.
	 *
	 * <p>
	 * To visit the pixels that contribute to an interpolated value, we move in
	 * a (binary-reflected) Gray code pattern, such that only one dimension of
	 * the target position is modified per move.
	 * </p>
	 * <p>
	 * See
	 * <a href="http://en.wikipedia.org/wiki/Gray_code">http://en.wikipedia.org/
	 * wiki/Gray_code</a>
	 * </p>
	 */
	@Override
	public abstract T get();

	@Override
	public T getType()
	{
		return type;
	}
}
