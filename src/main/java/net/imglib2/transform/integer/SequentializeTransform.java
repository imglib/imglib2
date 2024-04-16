/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2024 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.transform.integer;

import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.transform.InvertibleTransform;

/**
 * Transform n-dimensional to m-dimensional coordinates {@code (m<n)} by
 * flattening dimensions {@code >m}. An example of this transformation is the
 * way, a 2D image is flattened out as a 1D array in memory.
 *
 * @author Tobias Pietzsch
 */
public class SequentializeTransform implements InvertibleTransform
{
	/**
	 * dimension of source vector.
	 */
	protected final int numSourceDimensions;

	/**
	 * dimension of target vector.
	 */
	protected final int numTargetDimensions;

	protected final int maxSourceDimension;

	protected final int maxTargetDimension;

	protected final long[] seqDimensions;

	protected final InvertibleTransform inverse;

	public SequentializeTransform( final long[] sourceDimensions, final int numTargetDimensions )
	{
		this.numSourceDimensions = sourceDimensions.length;
		this.numTargetDimensions = numTargetDimensions;
		this.maxTargetDimension = numTargetDimensions - 1;
		this.maxSourceDimension = numSourceDimensions - 1;
		assert this.numSourceDimensions > this.numTargetDimensions;

		seqDimensions = new long[ numSourceDimensions ];
		for ( int d = maxTargetDimension; d < numSourceDimensions; ++d )
		{
			seqDimensions[ d ] = sourceDimensions[ d ];
		}

		inverse = new InvertibleTransform()
		{
			@Override
			public int numSourceDimensions()
			{
				return SequentializeTransform.this.numTargetDimensions();
			}

			@Override
			public int numTargetDimensions()
			{
				return SequentializeTransform.this.numSourceDimensions();
			}

			@Override
			public void apply( final long[] source, final long[] target )
			{
				SequentializeTransform.this.applyInverse( target, source );
			}

			@Override
			public void apply( final int[] source, final int[] target )
			{
				SequentializeTransform.this.applyInverse( target, source );
			}

			@Override
			public void apply( final Localizable source, final Positionable target )
			{
				SequentializeTransform.this.applyInverse( target, source );
			}

			@Override
			public void applyInverse( final long[] source, final long[] target )
			{
				SequentializeTransform.this.apply( target, source );
			}

			@Override
			public void applyInverse( final int[] source, final int[] target )
			{
				SequentializeTransform.this.apply( target, source );
			}

			@Override
			public void applyInverse( final Positionable source, final Localizable target )
			{
				SequentializeTransform.this.apply( target, source );
			}

			@Override
			public InvertibleTransform inverse()
			{
				return SequentializeTransform.this;
			}
		};
	}

	@Override
	public int numSourceDimensions()
	{
		return numSourceDimensions;
	}

	@Override
	public int numTargetDimensions()
	{
		return numTargetDimensions;
	}

	@Override
	public void apply( final long[] source, final long[] target )
	{
		assert source.length >= numSourceDimensions;
		assert target.length >= numTargetDimensions;

		for ( int d = 0; d < maxTargetDimension; ++d )
			target[ d ] = source[ d ];
		long i = source[ maxSourceDimension ];
		for ( int d = maxSourceDimension - 1; d >= maxTargetDimension; --d )
			i = i * seqDimensions[ d ] + source[ d ];
		target[ maxTargetDimension ] = i;
	}

	@Override
	public void apply( final int[] source, final int[] target )
	{
		assert source.length >= numSourceDimensions;
		assert target.length >= numTargetDimensions;

		for ( int d = 0; d < maxTargetDimension; ++d )
			target[ d ] = source[ d ];
		int i = source[ maxSourceDimension ];
		for ( int d = maxSourceDimension - 1; d >= maxTargetDimension; --d )
			i = i * ( int ) seqDimensions[ d ] + source[ d ];
		target[ maxTargetDimension ] = i;
	}

	@Override
	public void apply( final Localizable source, final Positionable target )
	{
		assert source.numDimensions() >= numSourceDimensions;
		assert target.numDimensions() >= numTargetDimensions;

		for ( int d = 0; d < maxTargetDimension; ++d )
			target.setPosition( source.getLongPosition( d ), d );
		long i = source.getLongPosition( maxSourceDimension );
		for ( int d = maxSourceDimension - 1; d >= maxTargetDimension; --d )
			i = i * seqDimensions[ d ] + source.getLongPosition( d );
		target.setPosition( i, maxTargetDimension );
	}

	@Override
	public void applyInverse( final long[] source, final long[] target )
	{
		assert source.length >= numSourceDimensions;
		assert target.length >= numTargetDimensions;

		for ( int d = 0; d < maxTargetDimension; ++d )
			source[ d ] = target[ d ];
		long i = target[ maxTargetDimension ];
		for ( int d = maxTargetDimension; d < maxSourceDimension; ++d )
		{
			final long j = i / seqDimensions[ d ];
			source[ d ] = i - j * seqDimensions[ d ];
			i = j;
		}
		source[ maxSourceDimension ] = i;
	}

	@Override
	public void applyInverse( final int[] source, final int[] target )
	{
		assert source.length >= numSourceDimensions;
		assert target.length >= numTargetDimensions;

		for ( int d = 0; d < maxTargetDimension; ++d )
			source[ d ] = target[ d ];
		int i = target[ maxTargetDimension ];
		for ( int d = maxTargetDimension; d < maxSourceDimension; ++d )
		{
			final int j = ( int ) ( i / seqDimensions[ d ] );
			source[ d ] = ( int ) ( i - j * seqDimensions[ d ] );
			i = j;
		}
		source[ maxSourceDimension ] = i;
	}

	@Override
	public void applyInverse( final Positionable source, final Localizable target )
	{
		assert source.numDimensions() >= numSourceDimensions;
		assert target.numDimensions() >= numTargetDimensions;

		for ( int d = 0; d < maxTargetDimension; ++d )
			source.setPosition( target.getLongPosition( d ), d );
		long i = target.getLongPosition( maxTargetDimension );
		for ( int d = maxTargetDimension; d < maxSourceDimension; ++d )
		{
			final long j = ( i / seqDimensions[ d ] );
			source.setPosition( i - j * seqDimensions[ d ], d );
			i = j;
		}
		source.setPosition( i, maxSourceDimension );
	}

	@Override
	public InvertibleTransform inverse()
	{
		return inverse;
	}
}
