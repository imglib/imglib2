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

package net.imglib2.type;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.NativeImg;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.cell.CellCursor;
import net.imglib2.img.cell.CellImg;
import net.imglib2.util.Fraction;

/**
 * A {@link NativeType} is a {@link Type} that that provides access to data
 * stored in Java primitive arrays. To this end, implementations maintain a
 * reference to the current storage array and the index of an element in that
 * array.
 *
 * The {@link NativeType} is positioned on the correct storage array and index
 * by accessors ({@link Cursor Cursors} and {@link RandomAccess RandomAccesses}
 * ).
 *
 * <p>
 * The {@link NativeType} is the only class that is aware of the actual data
 * type, i.e., which Java primitive type is used to store the data. On the other
 * hand it does not know the storage layout, i.e., how n-dimensional pixel
 * coordinates map to indices in the current array. It also doesn't know whether
 * and how the data is split into multiple chunks. This is determined by the
 * container implementation (e.g., {@link ArrayImg}, {@link CellImg}, ...).
 * Separating the storage layout from access and operations on the {@link Type}
 * avoids re-implementation for each container type.
 * </p>
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Tobias Pietzsch
 */
public interface NativeType< T extends NativeType< T > > extends Type< T >
{
	/**
	 * Get the number of entities in the storage array required to store one
	 * pixel value. A pixel value may be spread over several or less than one
	 * entity. For example, a complex number may require 2 entries of a float[]
	 * array to store one pixel. Or a 12-bit type might need 12/64th entries of
	 * a long[] array.
	 *
	 * @return the number of storage type entities required to store one pixel
	 *         value.
	 */
	public Fraction getEntitiesPerPixel();

	/**
	 * Creates a new {@link NativeType} which stores in the same physical array.
	 * This is only used internally.
	 *
	 * @return a new {@link NativeType} instance working on the same
	 *         {@link NativeImg}
	 */
	public T duplicateTypeOnSameNativeImg();

	public PrimitiveTypeInfo< T, ? > getPrimitiveTypeInfo();

	/**
	 * This method is used by an accessor (e.g., a {@link Cursor}) to request an
	 * update of the current data array.
	 *
	 * <p>
	 * As an example consider a {@link CellCursor} moving on a {@link CellImg}.
	 * The cursor maintains a {@link NativeType} which provides access to the
	 * image data. When the cursor moves from one cell to the next, the
	 * underlying data array of the {@link NativeType} must be switched to the
	 * data array of the new cell.
	 * </p>
	 * <p>
	 * To achieve this, the {@link CellCursor} calls {@code updateContainer()}
	 * with itself as the argument. {@code updateContainer()} in turn will call
	 * {@link NativeImg#update(Object)} on it's container, passing along the
	 * reference to the cursor. In this example, the container would be a
	 * {@link CellImg}. While the {@link NativeType} does not know about the
	 * type of the cursor, the container does. {@link CellImg} knows that it is
	 * passed a {@link CellCursor} instance, which can be used to figure out the
	 * current cell and the underlying data array, which is then returned to the
	 * {@link NativeType}.
	 * </p>
	 * <p>
	 * The idea behind this concept is maybe not obvious. The {@link NativeType}
	 * knows which basic type is used (float, int, byte, ...). However, it does
	 * not know how the data is stored ({@link ArrayImg}, {@link CellImg}, ...).
	 * This prevents the need for multiple implementations of {@link NativeType}
	 * .
	 * </p>
	 *
	 * @param c
	 *            reference to an accessor which can be passed on to the
	 *            container (which will know what to do with it).
	 */
	public void updateContainer( Object c );

	/**
	 * Set the index into the current data array.
	 *
	 * <p>
	 * This is used by accessors (e.g., a {@link Cursor}) to position the
	 * {@link NativeType} in the container.
	 *
	 * @param i
	 *            the new array index
	 */
	public void updateIndex( final int i );

	/**
	 * Get the current index into the current data array.
	 *
	 * <p>
	 * This is used by accessors (e.g., a {@link Cursor}) to position the
	 * {@link NativeType} in the container.
	 *
	 * @return the current index into the underlying data array
	 */
	public int getIndex();

	/**
	 * Increment the index into the current data array.
	 *
	 * <p>
	 * This is used by accessors (e.g., a {@link Cursor}) to position the
	 * {@link NativeType} in the container.
	 */
	public void incIndex();

	/**
	 * Increases the index into the current data array by {@code increment}
	 * steps.
	 *
	 * <p>
	 * This is used by accessors (e.g., a {@link Cursor}) to position the
	 * {@link NativeType} in the container.
	 *
	 * @param increment
	 *            how many steps
	 */
	public void incIndex( final int increment );

	/**
	 * Decrement the index into the current data array.
	 *
	 * <p>
	 * This is used by accessors (e.g., a {@link Cursor}) to position the
	 * {@link NativeType} in the container.
	 */
	public void decIndex();

	/**
	 * Decrease the index into the current data array by {@code decrement}
	 * steps.
	 *
	 * <p>
	 * This is used by accessors (e.g., a {@link Cursor}) to position the
	 * {@link NativeType} in the container.
	 *
	 * @param decrement
	 *            how many steps
	 */
	public void decIndex( final int decrement );
}
