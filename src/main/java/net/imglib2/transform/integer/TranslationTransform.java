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

package net.imglib2.transform.integer;

import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.concatenate.Concatenable;
import net.imglib2.concatenate.PreConcatenable;

/**
 * TODO
 * 
 */
public class TranslationTransform extends AbstractMixedTransform implements Translation, Concatenable< Translation >, PreConcatenable< Translation >
{
	/**
	 * target = source + translation.
	 */
	protected final long[] translation;

	protected final InverseTranslationTransform inverse;

	public TranslationTransform( final int targetDim )
	{
		super( targetDim );
		translation = new long[ targetDim ];
		this.inverse = new InverseTranslationTransform( targetDim );
	}

	public TranslationTransform( final long[] translation )
	{
		super( translation.length );
		this.translation = translation.clone();
		this.inverse = new InverseTranslationTransform( numTargetDimensions );
	}

	@Override
	public void getTranslation( final long[] t )
	{
		assert t.length == numTargetDimensions;
		for ( int d = 0; d < numTargetDimensions; ++d )
			t[ d ] = translation[ d ];
	}

	@Override
	public long getTranslation( final int d )
	{
		assert d <= numTargetDimensions;
		return translation[ d ];
	}

	public void setTranslation( final long[] t )
	{
		assert t.length == numTargetDimensions;
		for ( int d = 0; d < numTargetDimensions; ++d )
			translation[ d ] = t[ d ];
	}

	@Override
	public void apply( final long[] source, final long[] target )
	{
		assert source.length >= numTargetDimensions;
		assert target.length >= numTargetDimensions;

		for ( int d = 0; d < numTargetDimensions; ++d )
			target[ d ] = source[ d ] + translation[ d ];
	}

	@Override
	public void apply( final int[] source, final int[] target )
	{
		assert source.length >= numTargetDimensions;
		assert target.length >= numTargetDimensions;

		for ( int d = 0; d < numTargetDimensions; ++d )
			target[ d ] = source[ d ] + ( int ) translation[ d ];
	}

	@Override
	public void apply( final Localizable source, final Positionable target )
	{
		assert source.numDimensions() >= numTargetDimensions;
		assert target.numDimensions() >= numTargetDimensions;

		for ( int d = 0; d < numTargetDimensions; ++d )
			target.setPosition( source.getLongPosition( d ) + translation[ d ], d );
	}

	@Override
	public TranslationTransform concatenate( final Translation t )
	{
		assert numTargetDimensions == t.numTargetDimensions();

		final TranslationTransform result = new TranslationTransform( numTargetDimensions );
		for ( int d = 0; d < numTargetDimensions; ++d )
			result.translation[ d ] = this.translation[ d ] + t.getTranslation( d );
		return result;
	}

	@Override
	public Class< Translation > getConcatenableClass()
	{
		return Translation.class;
	}

	@Override
	public TranslationTransform preConcatenate( final Translation t )
	{
		return concatenate( t );
	}

	@Override
	public Class< Translation > getPreConcatenableClass()
	{
		return Translation.class;
	}

	/**
	 * set parameters to <code>transform</code>.
	 * 
	 * @param transform
	 */
	public void set( final Translation transform )
	{
		assert numTargetDimensions == transform.numTargetDimensions();

		transform.getTranslation( translation );
	}

	/**
	 * Get the matrix that transforms homogeneous source points to homogeneous
	 * target points. For testing purposes.
	 */
	@Override
	public double[][] getMatrix()
	{
		final double[][] mat = new double[ numTargetDimensions + 1 ][ numTargetDimensions + 1 ];

		mat[ numTargetDimensions ][ numTargetDimensions ] = 1;

		for ( int d = 0; d < numTargetDimensions; ++d )
		{
			mat[ d ][ numTargetDimensions ] = translation[ d ];
			mat[ d ][ d ] = 1;
		}

		return mat;
	}

	@Override
	public void applyInverse( final long[] source, final long[] target )
	{
		assert source.length >= numTargetDimensions;
		assert target.length >= numTargetDimensions;

		for ( int d = 0; d < numTargetDimensions; ++d )
			source[ d ] = target[ d ] - translation[ d ];
	}

	@Override
	public void applyInverse( final int[] source, final int[] target )
	{
		assert source.length >= numTargetDimensions;
		assert target.length >= numTargetDimensions;

		for ( int d = 0; d < numTargetDimensions; ++d )
			source[ d ] = target[ d ] - ( int ) translation[ d ];
	}

	@Override
	public void applyInverse( final Positionable source, final Localizable target )
	{
		assert source.numDimensions() >= numTargetDimensions;
		assert target.numDimensions() >= numTargetDimensions;

		for ( int d = 0; d < numTargetDimensions; ++d )
			source.setPosition( target.getLongPosition( d ) - translation[ d ], d );
	}

	public class InverseTranslationTransform extends AbstractMixedTransform implements Translation, Concatenable< Translation >, PreConcatenable< Translation >
	{
		InverseTranslationTransform( final int targetDim )
		{
			super( targetDim );
		}

		@Override
		public void apply( final long[] source, final long[] target )
		{
			TranslationTransform.this.applyInverse( target, source );
		}

		@Override
		public void apply( final int[] source, final int[] target )
		{
			TranslationTransform.this.applyInverse( target, source );
		}

		@Override
		public void apply( final Localizable source, final Positionable target )
		{
			TranslationTransform.this.applyInverse( target, source );
		}

		@Override
		public void applyInverse( final long[] source, final long[] target )
		{
			TranslationTransform.this.apply( target, source );
		}

		@Override
		public void applyInverse( final int[] source, final int[] target )
		{
			TranslationTransform.this.apply( target, source );
		}

		@Override
		public void applyInverse( final Positionable source, final Localizable target )
		{
			TranslationTransform.this.apply( target, source );
		}

		@Override
		public double[][] getMatrix()
		{
			final double[][] mat = new double[ numTargetDimensions + 1 ][ numTargetDimensions + 1 ];

			mat[ numTargetDimensions ][ numTargetDimensions ] = 1;

			for ( int d = 0; d < numTargetDimensions; ++d )
			{
				mat[ d ][ numTargetDimensions ] = -translation[ d ];
				mat[ d ][ d ] = 1;
			}

			return mat;
		}

		@Override
		public TranslationTransform inverse()
		{
			return TranslationTransform.this;
		}

		@Override
		public void getTranslation( final long[] t )
		{
			assert t.length == numTargetDimensions;
			for ( int d = 0; d < numTargetDimensions; ++d )
				t[ d ] = -TranslationTransform.this.translation[ d ];
		}

		@Override
		public long getTranslation( final int d )
		{
			assert d <= numTargetDimensions;
			return -TranslationTransform.this.translation[ d ];
		}

		@Override
		public TranslationTransform concatenate( final Translation t )
		{
			assert numTargetDimensions == t.numTargetDimensions();
			final TranslationTransform result = new TranslationTransform( numTargetDimensions );
			for ( int d = 0; d < numTargetDimensions; ++d )
				result.translation[ d ] = t.getTranslation( d ) - TranslationTransform.this.translation[ d ];
			return result;
		}

		@Override
		public Class< Translation > getConcatenableClass()
		{
			return Translation.class;
		}

		@Override
		public TranslationTransform preConcatenate( final Translation t )
		{
			return concatenate( t );
		}

		@Override
		public Class< Translation > getPreConcatenableClass()
		{
			return Translation.class;
		}
	}

	@Override
	public InverseTranslationTransform inverse()
	{
		return inverse;
	}
}
