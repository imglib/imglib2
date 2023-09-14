/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2023 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
/**
 * 
 */
package net.imglib2.transform.integer.shear;

import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.transform.integer.BoundingBox;

/**
 * Backward implementation of the most simple case of a shear transform:
 * coordinate[ shearDimension ] -= coordinate[ referenceDimension ]
 * 
 * 
 * @author Philipp Hanslovsky
 *
 */
public class InverseShearTransform extends AbstractShearTransform
{
	
	
	/**
	 * @param nDim					Number of dimensions (source and target dimensions must be the same)
	 * @param shearDimension		Dimension to be sheared.
	 * @param referenceDimension	Dimension used as reference for shear.
	 */
	public InverseShearTransform(
			int nDim,
			int shearDimension,
			int referenceDimension )
	{
		super( nDim, shearDimension, referenceDimension );
		this.inverse = new ShearTransform( 
				nDim, 
				shearDimension,
				referenceDimension,
				this );
	}

	
	/**
	 * Protected constructor for passing an inverse to avoid construction of unnecessary objects.
	 * 
	 * @param nDim					Number of dimensions (source and target dimensions must be the same)
	 * @param shearDimension		Dimension to be sheared.
	 * @param referenceDimension	Dimension used as reference for shear.
	 * @param inverse				
	 */
	protected InverseShearTransform( 
			int nDim, 
			int shearDimension,
			int referenceDimension, 
			AbstractShearTransform inverse )
	{
		super( nDim, shearDimension, referenceDimension, inverse );
	}


	@Override
	public void apply( long[] source, long[] target ) 
	{
		assert source.length >= this.nDim && target.length >= nDim;
		System.arraycopy( source, 0, target, 0, source.length );
		target[ shearDimension ] -= target[ referenceDimension ];
	}
	

	@Override
	public void apply( int[] source, int[] target )
	{
		assert source.length >= this.nDim && target.length >= nDim;
		System.arraycopy( source, 0, target, 0, source.length );
		target[ shearDimension ] -= target[ referenceDimension ];
	}
	

	@Override
	public void apply( Localizable source, Positionable target ) 
	{
		assert source.numDimensions() >= this.nDim && target.numDimensions() >= nDim;
		target.setPosition( source );
		target.setPosition( source.getLongPosition( shearDimension ) - source.getLongPosition( referenceDimension ), shearDimension );
	}

	@Override
	public AbstractShearTransform copy()
	{
		return new InverseShearTransform( nDim, shearDimension, referenceDimension ); // return this; ?
	}


	@Override
	public long[] getShear() 
	{
		long[] shear = new long[ this.nDim ];
		shear[ this.shearDimension ] = -1;
		return shear;
	}

	
	@Override public BoundingBox transform( BoundingBox bb )
	{
		bb.orderMinMax();
		long[] c = bb.corner1;
		// assume that corner2[ i ] > corner1[ i ]
		long diff = bb.corner2[ this.referenceDimension ] - c[ this.referenceDimension ];
		c[ this.shearDimension ] -=diff;
		return bb;
	}

	
}
