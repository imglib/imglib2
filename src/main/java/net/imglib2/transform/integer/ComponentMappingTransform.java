/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2015 Tobias Pietzsch, Stephan Preibisch, Barry DeZonia,
 * Stephan Saalfeld, Curtis Rueden, Albert Cardona, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Jonathan Hale, Lee Kamentsky, Larry Lindsey, Mark
 * Hiner, Michael Zinsmaier, Martin Horn, Grant Harris, Aivar Grislis, John
 * Bogovic, Steffen Jaensch, Stefan Helfrich, Jan Funke, Nick Perry, Mark Longair,
 * Melissa Linkert and Dimiter Prodanov.
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
 * Map the components of the source vector to obtain the target vector, for
 * instance transform (x,y,z) to (x,z,y).
 * 
 * <p>
 * The intended use of ComponentMapping is as a dimension permutation. The
 * mapping is implemented as a inverse lookup, i.e., every component of the
 * target is read from a source component.
 * <em>Note, that it is not allowed to set this array such that a source component
 * is mapped to several target components!</em>
 * </p>
 * 
 * 
 * @author Tobias Pietzsch
 */
public class ComponentMappingTransform extends AbstractMixedTransform implements ComponentMapping, Concatenable< ComponentMapping >, PreConcatenable< ComponentMapping >
{
	/**
	 * for each component of the target vector: from which source vector
	 * component should it be taken.
	 */
	protected final int[] component;

	public ComponentMappingTransform( final int targetDim )
	{
		super( targetDim );
		component = new int[ targetDim ];
		for ( int d = 0; d < targetDim; ++d )
			component[ d ] = d;
	}

	/**
	 * @param component
	 *            array specifying for each component of the target vector from
	 *            which source vector component should it be taken.
	 */
	public ComponentMappingTransform( final int[] component )
	{
		super( component.length );
		this.component = component.clone();
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
	 * For instance, if the transform maps 3D (x,y,z) coordinates to 2D (z,x,y)
	 * coordinate this will be [2, 0, 1].
	 * </p>
	 * 
	 * @param component
	 *            array that says for each component of the target vector from
	 *            which source vector component it should be taken.
	 */
	public void setComponentMapping( final int[] component )
	{
		assert component.length >= numTargetDimensions;
		for ( int d = 0; d < numTargetDimensions; ++d )
			this.component[ d ] = component[ d ];
	}

	@Override
	public double[][] getMatrix()
	{
		final double[][] mat = new double[ numTargetDimensions + 1 ][ numTargetDimensions + 1 ];

		mat[ numTargetDimensions ][ numTargetDimensions ] = 1;

		for ( int d = 0; d < numTargetDimensions; ++d )
		{
			mat[ d ][ component[ d ] ] = 1;
		}

		return mat;
	}

	@Override
	public void apply( final long[] source, final long[] target )
	{
		assert source.length >= numTargetDimensions;
		assert target.length >= numTargetDimensions;

		for ( int d = 0; d < numTargetDimensions; ++d )
			target[ d ] = source[ component[ d ] ];
	}

	@Override
	public void apply( final int[] source, final int[] target )
	{
		assert source.length >= numTargetDimensions;
		assert target.length >= numTargetDimensions;

		for ( int d = 0; d < numTargetDimensions; ++d )
			target[ d ] = source[ component[ d ] ];
	}

	@Override
	public void apply( final Localizable source, final Positionable target )
	{
		assert source.numDimensions() >= numTargetDimensions;
		assert target.numDimensions() >= numTargetDimensions;

		for ( int d = 0; d < numTargetDimensions; ++d )
			target.setPosition( source.getLongPosition( component[ d ] ), d );
	}

	@Override
	public ComponentMappingTransform concatenate( final ComponentMapping t )
	{
		assert numTargetDimensions == t.numTargetDimensions();

		final ComponentMappingTransform result = new ComponentMappingTransform( numTargetDimensions );

		for ( int d = 0; d < numTargetDimensions; ++d )
			result.component[ d ] = t.getComponentMapping( this.component[ d ] );

		return result;
	}

	@Override
	public Class< ComponentMapping > getConcatenableClass()
	{
		return ComponentMapping.class;
	}

	@Override
	public ComponentMappingTransform preConcatenate( final ComponentMapping t )
	{
		assert numTargetDimensions == t.numTargetDimensions();

		final ComponentMappingTransform result = new ComponentMappingTransform( numTargetDimensions );

		for ( int d = 0; d < numTargetDimensions; ++d )
			result.component[ d ] = this.component[ t.getComponentMapping( d ) ];

		return result;
	}

	@Override
	public Class< ComponentMapping > getPreConcatenableClass()
	{
		return ComponentMapping.class;
	}
}
