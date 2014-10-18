/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
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
 * Map the components of the source vector to a slice of the target space, for
 * instance transform (x,y) to (x,C,y) where C is a constant.
 * 
 * <p>
 * A {@link SlicingTransform} transform a n-dimensional source vector to a
 * m-dimensional target vector, where m >= n. It can be represented as a
 * <em>m+1</em> &times; <em>n+1</em> homogeneous matrix. The
 * {@link SlicingTransform} can be decomposed as follows:
 * <ol>
 * <li>component permutation</li>
 * <li>project up & position (add constant components in the target vector)</li>
 * </ol>
 * </p>
 * 
 * <p>
 * The component permutation step is implemented by the
 * {@link #setComponentMapping(int[]) component mapping}. This is a lookup array
 * that specifies for each target dimension from which source dimension it is
 * taken.
 * <em>Note, that it is not allowed to set this array such that a source component
 * is mapped to several target components!</em>
 * </p>
 * 
 * 
 * @author Tobias Pietzsch
 */
public class SlicingTransform extends AbstractMixedTransform implements Slicing, Concatenable< Slicing >, PreConcatenable< Slicing >
{
	/**
	 * dimension of source vector.
	 */
	protected final int numSourceDimensions;

	/**
	 * for each component of the target vector (before translation). should the
	 * value be taken from a source vector component (false) or should it be
	 * zero (true).
	 */
	protected final boolean[] zero;

	/**
	 * translation is added to the target vector after applying the permutation
	 * and project-up operations. Only translation values for dimensions that
	 * have not been assigned a source vector component are used. For instance,
	 * if you project (x,y) to (x,y,z) only the translation value for z is used.
	 */
	protected final long[] translation;

	/**
	 * specifies for each component of the target vector from which source
	 * vector component should it be taken.
	 */
	protected final int[] component;

	public SlicingTransform( final int sourceDim, final int targetDim )
	{
		super( targetDim );
		assert sourceDim <= targetDim;

		numSourceDimensions = sourceDim;
		translation = new long[ targetDim ];
		zero = new boolean[ targetDim ];
		component = new int[ targetDim ];
		for ( int d = 0; d < targetDim; ++d )
		{
			if ( d < sourceDim )
			{
				component[ d ] = d;
			}
			else
			{
				component[ d ] = 0;
				zero[ d ] = true;
			}
		}
	}

	@Override
	public int numSourceDimensions()
	{
		return numSourceDimensions;
	}

	@Override
	public void getTranslation( final long[] t )
	{
		assert t.length == numTargetDimensions;
		for ( int d = 0; d < numTargetDimensions; ++d )
			t[ d ] = zero[ d ] ? translation[ d ] : 0;
	}

	@Override
	public long getTranslation( final int d )
	{
		assert d <= numTargetDimensions;
		return zero[ d ] ? translation[ d ] : 0;
	}

	public void setTranslation( final long[] t )
	{
		assert t.length == numTargetDimensions;
		for ( int d = 0; d < numTargetDimensions; ++d )
			translation[ d ] = t[ d ];
	}

	@Override
	public void getComponentZero( final boolean[] zero )
	{
		assert zero.length >= numTargetDimensions;
		for ( int d = 0; d < numTargetDimensions; ++d )
			zero[ d ] = this.zero[ d ];
	}

	@Override
	public boolean getComponentZero( final int d )
	{
		assert d <= numTargetDimensions;
		return zero[ d ];
	}

	/**
	 * Set which target dimensions are _not_ taken from source dimensions.
	 * 
	 * <p>
	 * For instance, if the transform maps 2D (x,y) coordinates to the first two
	 * components of a 3D (x,y,z) coordinate, this will be [false, false, true]
	 * </p>
	 * 
	 * @param zero
	 *            array that says for each component of the target vector
	 *            (before translation) whether the value should be taken from a
	 *            source vector component (false) or should be set to zero
	 *            (true).
	 */
	public void setComponentZero( final boolean[] zero )
	{
		assert zero.length >= numTargetDimensions;
		for ( int d = 0; d < numTargetDimensions; ++d )
			this.zero[ d ] = zero[ d ];
	}

	@Override
	public void getComponentMapping( final int[] component )
	{
		assert component.length >= numTargetDimensions;
		for ( int d = 0; d < numTargetDimensions; ++d )
			component[ d ] = this.component[ d ];
	}

	@Override
	public int getComponentMapping( final int d )
	{
		assert d <= numTargetDimensions;
		return component[ d ];
	}

	/**
	 * Set for each target dimensions from which source dimension it is taken.
	 * 
	 * <p>
	 * For instance, if the transform maps 2D (x,y) coordinates to the first two
	 * components of a 3D (x,y,z) coordinate, this will be [0, 1, x]. Here, x
	 * can be any value because the third target dimension does not correspond
	 * to any source dimension, which can be realized using
	 * {@link #setZero(boolean[])}.
	 * </p>
	 * 
	 * <p>
	 * <em>Note, that it is not allowed to set the {@code component} array such that
	 * a source component is mapped to several target components!</em>
	 * </p>
	 * 
	 * @param component
	 *            array that says for each component of the target vector
	 *            (before translation) from which source vector component it
	 *            should be taken.
	 */
	public void setComponentMapping( final int[] component )
	{
		assert component.length >= numTargetDimensions;
		for ( int d = 0; d < numTargetDimensions; ++d )
			this.component[ d ] = component[ d ];
	}

	@Override
	public void apply( final long[] source, final long[] target )
	{
		assert source.length >= numSourceDimensions;
		assert target.length >= numTargetDimensions;

		for ( int d = 0; d < numTargetDimensions; ++d )
			target[ d ] = zero[ d ] ? translation[ d ] : source[ component[ d ] ];
	}

	@Override
	public void apply( final int[] source, final int[] target )
	{
		assert source.length >= numSourceDimensions;
		assert target.length >= numTargetDimensions;

		for ( int d = 0; d < numTargetDimensions; ++d )
			target[ d ] = zero[ d ] ? ( int ) translation[ d ] : source[ component[ d ] ];
	}

	@Override
	public void apply( final Localizable source, final Positionable target )
	{
		assert source.numDimensions() >= numSourceDimensions;
		assert target.numDimensions() >= numTargetDimensions;

		for ( int d = 0; d < numTargetDimensions; ++d )
			target.setPosition( zero[ d ] ? ( int ) translation[ d ] : source.getLongPosition( component[ d ] ), d );
	}

	@Override
	public SlicingTransform concatenate( final Slicing t )
	{
		assert this.numSourceDimensions == t.numTargetDimensions();

		final SlicingTransform result = new SlicingTransform( t.numSourceDimensions(), this.numTargetDimensions );

		for ( int d = 0; d < result.numTargetDimensions; ++d )
		{
			if ( this.zero[ d ] )
			{
				result.zero[ d ] = true;
				result.translation[ d ] = this.translation[ d ];
			}
			else
			{
				final int c = this.component[ d ];
				if ( t.getComponentZero( c ) )
				{
					result.zero[ d ] = true;
					result.translation[ d ] = t.getTranslation( c );
				}
				else
				{
					result.zero[ d ] = false;
					result.component[ d ] = t.getComponentMapping( c );
				}
			}
		}
		return result;
	}

	@Override
	public Class< Slicing > getConcatenableClass()
	{
		return Slicing.class;
	}

	@Override
	public SlicingTransform preConcatenate( final Slicing t )
	{
		assert t.numSourceDimensions() == this.numTargetDimensions;

		final SlicingTransform result = new SlicingTransform( this.numSourceDimensions, t.numTargetDimensions() );

		for ( int d = 0; d < result.numTargetDimensions; ++d )
		{
			if ( t.getComponentZero( d ) )
			{
				result.zero[ d ] = true;
				result.translation[ d ] = t.getTranslation( d );
			}
			else
			{
				final int c = t.getComponentMapping( d );
				if ( this.zero[ c ] )
				{
					result.zero[ d ] = true;
					result.translation[ d ] = this.translation[ c ];
				}
				else
				{
					result.zero[ d ] = false;
					result.component[ d ] = this.component[ c ];
				}
			}
		}
		return result;
	}

	@Override
	public Class< Slicing > getPreConcatenableClass()
	{
		return Slicing.class;
	}

	/**
	 * set parameters to <code>transform</code>.
	 * 
	 * @param transform
	 */
	public void set( final Slicing transform )
	{
		assert numSourceDimensions == transform.numSourceDimensions();
		assert numTargetDimensions == transform.numTargetDimensions();

		transform.getTranslation( translation );
		transform.getComponentZero( zero );
		transform.getComponentMapping( component );
	}

	/**
	 * Get the matrix that transforms homogeneous source points to homogeneous
	 * target points. For testing purposes.
	 */
	@Override
	public double[][] getMatrix()
	{
		final double[][] mat = new double[ numTargetDimensions + 1 ][ numSourceDimensions + 1 ];

		mat[ numTargetDimensions ][ numSourceDimensions ] = 1;

		for ( int d = 0; d < numTargetDimensions; ++d )
			mat[ d ][ numSourceDimensions ] = getTranslation( d );

		for ( int d = 0; d < numTargetDimensions; ++d )
			if ( zero[ d ] == false )
				mat[ d ][ component[ d ] ] = 1;

		return mat;
	}
}
