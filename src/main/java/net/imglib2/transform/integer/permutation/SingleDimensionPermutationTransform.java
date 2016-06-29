/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
package net.imglib2.transform.integer.permutation;

import net.imglib2.Localizable;
import net.imglib2.Positionable;

/**
 * @author Philipp Hanslovsky (hanslovskyp@janelia.hhmi.org)
 *
 *         Apply bijective permutation to one coordinate axis only.
 *
 */
public class SingleDimensionPermutationTransform extends AbstractPermutationTransform
{

	final protected int numSourceDimensions;

	final protected int numTargetDimensions;

	final protected int d;

	/**
	 * @param lut
	 * @param numSourceDimensions
	 *            dimensionality of source
	 * @param numTargetDimensions
	 *            dimensionality of target
	 * @param d
	 *            dimension which shall be transformed. Must be smaller than
	 *            {@link #numSourceDimensions} and {@link #numTargetDimensions}
	 */
	public SingleDimensionPermutationTransform(
			final int[] lut,
			final int numSourceDimensions,
			final int numTargetDimensions,
			final int d )
	{
		super( lut );
		assert d > 0 && d < numTargetDimensions && d < numSourceDimensions;
		this.numSourceDimensions = numSourceDimensions;
		this.numTargetDimensions = numTargetDimensions;
		this.d = d;
	}

	@Override
	public void applyInverse( final long[] source, final long[] target )
	{
		System.arraycopy( target, 0, source, 0, this.numSourceDimensions );
		source[ this.d ] = this.applyInverse( ( int ) target[ this.d ] );
	}

	@Override
	public void applyInverse( final int[] source, final int[] target )
	{
		System.arraycopy( target, 0, source, 0, this.numSourceDimensions );
		source[ this.d ] = this.applyInverse( target[ this.d ] );
	}

	@Override
	public void applyInverse( final Positionable source, final Localizable target )
	{
		source.setPosition( target );
		source.setPosition( this.applyInverse( target.getIntPosition( this.d ) ), this.d );
	}

	@Override
	public SingleDimensionPermutationTransform inverse()
	{
		return new SingleDimensionPermutationTransform( this.inverseLut, this.numSourceDimensions, this.numTargetDimensions, this.d );
	}

	@Override
	public int numSourceDimensions()
	{
		return this.numSourceDimensions;
	}

	@Override
	public int numTargetDimensions()
	{
		return this.numTargetDimensions;
	}

	@Override
	public void apply( final long[] source, final long[] target )
	{
		System.arraycopy( source, 0, target, 0, this.numTargetDimensions );
		target[ this.d ] = this.apply( ( int ) source[ this.d ] );
	}

	@Override
	public void apply( final int[] source, final int[] target )
	{
		System.arraycopy( source, 0, target, 0, this.numTargetDimensions );
		target[ this.d ] = this.apply( source[ this.d ] );
	}

	@Override
	public void apply( final Localizable source, final Positionable target )
	{
		target.setPosition( source );
		target.setPosition( this.apply( source.getIntPosition( this.d ) ), this.d );
	}

}
