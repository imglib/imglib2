/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2022 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

/**
 * An element that can be positioned in n-dimensional discrete space.
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Tobias Pietzsch
 */
public interface Positionable extends EuclideanSpace
{
	/**
	 * Move by 1 in one dimension.
	 *
	 * @param d
	 *            dimension
	 */
	void fwd( int d );

	/**
	 * Move by -1 in one dimension.
	 *
	 * @param d
	 *            dimension
	 */
	void bck( int d );

	/**
	 * Move the element in one dimension for some distance.
	 *
	 * @param distance
	 *            relative offset in dimension d
	 * @param d
	 *            dimension
	 */
	void move( int distance, int d );

	/**
	 * Move the element in one dimension for some distance.
	 *
	 * @param distance
	 *            relative offset in dimension d
	 * @param d
	 *            dimension
	 */
	void move( long distance, int d );

	/**
	 * Move the element relative to its current location using an
	 * {@link Localizable} as distance vector.
	 *
	 * @param distance
	 *            relative offset, {@link Localizable#numDimensions()} must be
	 *            &ge; {@link #numDimensions()}
	 */
	void move( Localizable distance );

	/**
	 * Move the element relative to its current location using an int[] as
	 * distance vector.
	 *
	 * @param distance
	 *            relative offset, length must be &ge; {@link #numDimensions()}
	 */
	void move( int[] distance );

	/**
	 * Move the element relative to its current location using a long[] as
	 * distance vector.
	 *
	 * @param distance
	 *            relative offset, length must be &ge; {@link #numDimensions()}
	 */
	void move( long[] distance );

	/**
	 * Place the element at the same location as a given {@link Localizable}
	 *
	 * @param position
	 *            absolute position, {@link Localizable#numDimensions()} must be
	 *            &ge; {@link #numDimensions()}
	 */
	void setPosition( Localizable position );

	/**
	 * Set the position of the element.
	 *
	 * @param position
	 *            absolute position, length must be &ge;
	 *            {@link #numDimensions()}
	 */
	void setPosition( int[] position );

	/**
	 * Set the position of the element.
	 *
	 * @param position
	 *            absolute position, length must be &ge;
	 *            {@link #numDimensions()}
	 */
	void setPosition( long[] position );

	/**
	 * Set the position of the element for one dimension.
	 *
	 * @param position
	 *            absolute position in dimension d
	 * @param d
	 *            dimension
	 */
	void setPosition( int position, int d );

	/**
	 * Set the position of the element for one dimension.
	 *
	 * @param position
	 *            absolute position in dimension d
	 * @param d
	 *            dimension
	 */
	void setPosition( long position, int d );
}
