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

import java.util.Arrays;

import net.imglib2.exception.InvalidDimensionsException;

/**
 * Defines an extent in <em>n</em>-dimensional discrete space.
 *
 * @author Tobias Pietzsch
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Philipp Hanslovsky
 */
public interface Dimensions extends EuclideanSpace
{
	/**
	 * Write the number of pixels in each dimension into long[].
	 *
	 * @param dimensions
	 */
	default void dimensions( final long[] dimensions )
	{
		final int n = numDimensions();
		for ( int d = 0; d < n; d++ )
			dimensions[ d ] = dimension( d );
	}

	/**
	 * Write the number of pixels in each dimension into {@link Positionable}.
	 *
	 * @param dimensions
	 */
	default void dimensions( final Positionable dimensions )
	{
		final int n = numDimensions();
		for ( int d = 0; d < n; d++ )
			dimensions.setPosition( dimension( d ), d );
	}

	/**
	 * Get the number of pixels in a given dimension <em>d</em>.
	 *
	 * @param d
	 */
	public long dimension( int d );

	/**
	 * Allocates a new long array with the dimensions of this object.
	 *
	 * Please note that his method allocates a new array each time which
	 * introduces notable overhead in both compute and memory.
	 * If you query it frequently, you should allocate a dedicated array
	 * first and reuse it with {@link #dimensions(long[])}.
	 *
	 * @return the dimensions
	 */
	default long[] dimensionsAsLongArray()
	{
		final long[] dims = new long[ numDimensions() ];
		dimensions( dims );
		return dims;
	}

	/**
	 * Allocates a new {@link Point} with the dimensions of this object.
	 *
	 * Please note that his method allocates a new {@link Point} each time
	 * which introduces notable overhead in both compute and memory.
	 * If you query it frequently, you should allocate a dedicated
	 * {@link Point} first and reuse it with {@link #dimensions(Positionable)}.
	 *
	 * @return the dimensions
	 */
	default Point dimensionsAsPoint()
	{
		return new Point( dimensionsAsLongArray() );
	}

	/*
	 * -----------------------------------------------------------------------
	 *
	 * Static methods
	 *
	 * -----------------------------------------------------------------------
	 */

	/**
	 * Check whether all entries in {@code dimensions} are positive
	 *
	 * @param dimensions
	 * @return true if all entries in {@code dimension} are positive, false
	 *         otherwise
	 */
	static boolean allPositive( final long... dimensions )
	{
		for ( final long d : dimensions )
			if ( d < 1 )
				return false;
		return true;
	}

	/**
	 * Check whether all entries in {@code dimensions} are positive
	 *
	 * @param dimensions
	 * @return true if all entries in {@code dimension} are positive, false
	 *         otherwise
	 */
	static boolean allPositive( final int... dimensions )
	{
		for ( final int d : dimensions )
			if ( d < 1 )
				return false;
		return true;
	}

	/**
	 * Check that all entries in {@code dimensions} are positive
	 *
	 * @param dimensions
	 * @return {@code dimensions}
	 * @throws InvalidDimensionsException
	 *             if any of {@code dimensions} is not positive (zero or
	 *             negative).
	 */
	static long[] verifyAllPositive( final long... dimensions ) throws InvalidDimensionsException
	{
		if ( !Dimensions.allPositive( dimensions ) )
			throw new InvalidDimensionsException(
					dimensions,
					"Expected only positive dimensions but got: " + Arrays.toString( dimensions ) );
		return dimensions;
	}

	/**
	 * Check that all entries in {@code dimensions} are positive
	 *
	 * @param dimensions
	 * @return {@code dimensions}
	 * @throws InvalidDimensionsException
	 *             if any of {@code dimensions} is not positive (zero or
	 *             negative).
	 */
	static int[] verifyAllPositive( final int... dimensions ) throws InvalidDimensionsException
	{
		if ( !Dimensions.allPositive( dimensions ) )
			throw new InvalidDimensionsException(
					dimensions,
					"Expected only positive dimensions but got: " + Arrays.toString( dimensions ) );
		return dimensions;
	}

	/**
	 * Verify that {@code dimensions} is not null or empty, and that all
	 * dimensions are positive. Throw {@link InvalidDimensionsException} otherwise.
	 *
	 * @param dimensions
	 *            to be verified.
	 * @return {@code dimensions} if successfully verified.
	 * @throws IllegalArgumentException
	 *             if {@code dimensions == null} or
	 *             {@code dimensions.length == 0} or any dimensions is zero or
	 *             negative.
	 */
	static long[] verify( final long... dimensions ) throws InvalidDimensionsException
	{
		if ( dimensions == null )
			throw new InvalidDimensionsException( dimensions, "Dimensions are null." );

		if ( dimensions.length == 0 )
			throw new InvalidDimensionsException( dimensions, "Dimensions are zero length." );

		return verifyAllPositive( dimensions );
	}

	/**
	 * Verify that {@code dimensions} is not null or empty, and that all
	 * dimensions are positive. Throw {@link InvalidDimensionsException} otherwise.
	 *
	 * @param dimensions
	 *            to be verified.
	 * @return {@code dimensions} if successfully verified.
	 * @throws IllegalArgumentException
	 *             if {@code dimensions == null} or
	 *             {@code dimensions.length == 0} or any dimensions is zero or
	 *             negative.
	 */
	static int[] verify( final int... dimensions ) throws InvalidDimensionsException
	{
		if ( dimensions == null )
			throw new InvalidDimensionsException( dimensions, "Dimensions are null." );

		if ( dimensions.length == 0 )
			throw new InvalidDimensionsException( dimensions, "Dimensions are zero length." );

		return verifyAllPositive( dimensions );
	}
}
