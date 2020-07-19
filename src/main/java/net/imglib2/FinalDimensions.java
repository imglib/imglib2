/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2020 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2;

import net.imglib2.util.Intervals;

import java.util.Arrays;

/**
 * An implementation of dimensionality that can wrap a long[] array. The same
 * principle for wrapping as in Point is used.
 *
 * @author Stephan Preibisch
 */
public final class FinalDimensions implements Dimensions
{
	final long[] dimensions;

	/**
	 * Protected constructor that can re-use the passed position array.
	 *
	 * @param dimensions
	 *            array used to store the position.
	 * @param copy
	 *            flag indicating whether position array should be duplicated.
	 */
	protected FinalDimensions( final long[] dimensions, final boolean copy )
	{
		if ( copy )
			this.dimensions = dimensions.clone();
		else
			this.dimensions = dimensions;
	}

	/**
	 * Create a FinalDimensions with a defined size
	 *
	 * @param dimensions
	 *            the size
	 */
	public FinalDimensions( final long... dimensions )
	{
		this( dimensions, true );
	}

	/**
	 * Create a FinalDimensions with a defined size
	 *
	 * @param dimensions
	 *            the size
	 */
	public FinalDimensions( final int... dimensions )
	{
		this.dimensions = new long[ dimensions.length ];

		for ( int d = 0; d < dimensions.length; ++d )
			this.dimensions[ d ] = dimensions[ d ];
	}

	/**
	 * Create a FinalDimensions with a defined size
	 *
	 * @param dimensions
	 *            the size
	 */
	public FinalDimensions( final Dimensions dimensions )
	{
		this( Intervals.dimensionsAsLongArray( dimensions ), false );
	}

	@Override
	public int numDimensions()
	{
		return dimensions.length;
	}

	@Override
	public void dimensions( final long[] dims )
	{
		for ( int d = 0; d < dims.length; ++d )
			dims[ d ] = this.dimensions[ d ];
	}

	@Override
	public long dimension( final int d )
	{
		return dimensions[ d ];
	}

	@Override
	public String toString()
	{
		return Intervals.toString( this );
	}

	@Override
	public boolean equals( final Object obj )
	{
		return obj instanceof FinalDimensions &&
				Intervals.equalDimensions( this, (FinalDimensions) obj );
	}

	@Override
	public int hashCode()
	{
		return Arrays.hashCode( dimensions );
	}

	/**
	 * Create a FinalDimensions object that stores its coordinates in the
	 * provided position array.
	 *
	 * @param dimensions
	 *            array to use for storing the position.
	 */
	public static FinalDimensions wrap( final long[] dimensions )
	{
		return new FinalDimensions( dimensions, false );
	}
}
