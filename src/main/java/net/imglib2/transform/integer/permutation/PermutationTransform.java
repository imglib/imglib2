/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2018 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.Positionable;

/**
 * Bijective integer transform mapping between integer coordinates in [0,n-1].
 *
 * @author Stephan Saalfeld
 * @author Philipp Hanslovsky
 */
public class PermutationTransform extends AbstractPermutationTransform
{
	final protected int numSourceDimensions;

	final protected int numTargetDimensions;

	/**
	 *
	 * @param lut
	 *            must be a bijective permutation over its index set, i.e. for a
	 *            lut of length <em>n</em>, the sorted content the array must be
	 *            [0,...,n-1] which is the index set of the lut.
	 * @param numSourceDimensions
	 * @param numTargetDimensions
	 */
	public PermutationTransform( final int[] lut, final int numSourceDimensions, final int numTargetDimensions )
	{
		super( lut );
		this.numSourceDimensions = numSourceDimensions;
		this.numTargetDimensions = numTargetDimensions;

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
		assert source.length >= this.numTargetDimensions && target.length >= this.numTargetDimensions: "Dimensions do not match.";

		for ( int d = 0; d < this.numTargetDimensions; ++d )
			target[ d ] = this.apply( ( int ) source[ d ] );
	}

	@Override
	public void apply( final int[] source, final int[] target )
	{
		assert source.length >= this.numTargetDimensions && target.length >= this.numTargetDimensions: "Dimensions do not match.";

		for ( int d = 0; d < this.numTargetDimensions; ++d )
			target[ d ] = this.apply( this.lut[ source[ d ] ] );
	}

	@Override
	public void apply( final Localizable source, final Positionable target )
	{
		assert source.numDimensions() >= this.numTargetDimensions && target.numDimensions() >= this.numTargetDimensions: "Dimensions do not match.";

		for ( int d = 0; d < this.numTargetDimensions; ++d )
			target.setPosition( this.apply( source.getIntPosition( d ) ), d );
	}

	@Override
	public void applyInverse( final long[] source, final long[] target )
	{
		assert source.length >= this.numSourceDimensions && target.length >= this.numSourceDimensions: "Dimensions do not match.";

		for ( int d = 0; d < this.numSourceDimensions; ++d )
			source[ d ] = this.applyInverse( ( int ) target[ d ] );
	}

	@Override
	public void applyInverse( final int[] source, final int[] target )
	{
		assert source.length >= this.numSourceDimensions && target.length >= this.numSourceDimensions: "Dimensions do not match.";

		for ( int d = 0; d < this.numSourceDimensions; ++d )
			source[ d ] = this.applyInverse( target[ d ] );
	}

	@Override
	public void applyInverse( final Positionable source, final Localizable target )
	{
		assert source.numDimensions() >= this.numSourceDimensions && target.numDimensions() >= this.numSourceDimensions: "Dimensions do not match.";

		for ( int d = 0; d < this.numSourceDimensions; ++d )
			source.setPosition( this.applyInverse( target.getIntPosition( d ) ), d );
	}

	@Override
	public PermutationTransform inverse()
	{
		return new PermutationTransform( this.inverseLut, this.numTargetDimensions, this.numSourceDimensions );
	}

	/**
	 * Test whether a given lut can be applied to an interval. This means that
	 * the interval is a hypercube at min = 0<sup><em>n</em></sup> with size =
	 * lut.length.
	 *
	 * @param interval
	 * @param lut
	 * @return
	 */
	static public boolean checkInterval( final Interval interval, final int[] lut )
	{
		for ( int d = 0; d < interval.numDimensions(); ++d )
		{
			if ( !( interval.min( d ) == 0 && interval.dimension( d ) == lut.length ) )
				return false;
		}
		return true;
	}

}
