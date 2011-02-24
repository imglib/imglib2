/**
 * Copyright (c) 2009--2010, Stephan Preibisch & Stephan Saalfeld
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the Fiji project nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package mpicbg.imglib.type;

import mpicbg.imglib.container.NativeContainer;
import mpicbg.imglib.container.NativeContainerFactory;

/**
 * A {@link NativeType} is a {@link Type} that that provides access to data
 * stored in native arrays, where the data may be split into chunks across
 * multiple arrays. To this end, implementations maintain a reference to the
 * current array (a {@link DataAccess}) and the index of an element in the
 * current array.
 * 
 * The {@link NativeType} is positioned on the correct data array and index by
 * accessors (e.g., {@link Cursor Cursors}, {@link RandomAccess RandomAccesses},
 * etc.)
 * 
 * <p>
 * The {@link NativeType} is the only class that is aware of the actual data type,
 * i.e., which basic type ({@link DataAccess}) is used to store the data. On the
 * other hand it does not know the storage layout, i.e., whether and how
 * the data is split into multiple chunks. This is determined by the container
 * (e.g., {@link Array}, {@link CellContainer}, etc.).
 * Separating the storage layout from access and operations on the {@link Type}
 * avoids re-implementation for each container type.
 * 
 * @author Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld
 */
public interface NativeType<T extends NativeType<T>> extends Type<T>
{	
	public int getEntitiesPerPixel(); 

	/**
	 * The {@link NativeType} creates the {@link NativeContainer} used for storing image
	 * data; based on the given storage strategy and its size. It basically only
	 * decides here which BasicType it uses (float, int, byte, bit, ...) and how
	 * many entities per pixel it needs (e.g. 2 floats per pixel for a complex
	 * number). This enables the separation of containers and the basic
	 * types.
	 * 
	 * @param storageFactory  which storage strategy is used
	 * @param dim             the dimensions
	 * 
	 * @return the instantiated NativeContainer where only the
	 *         {@link Type} knows the BasicType it contains.
	 */
	public NativeContainer< T, ? > createSuitableNativeContainer( final NativeContainerFactory< T > storageFactory, final long[] dim );
	
	/**
	 * Creates a new {@link NativeType} which stores in the same physical array. This
	 * is only used internally.
	 * 
	 * @return - a new {@link NativeType} instance working on the same
	 *         {@link NativeContainer}
	 */
	public T duplicateTypeOnSameNativeContainer();	

	/**
	 * This method is used by an accessor (e.g., a {@link Cursor})
	 * to request an update of the current data array.
	 * 
	 * <p>
	 * As an example consider a {@link CellCursor} moving on a {@link CellContainer}.
	 * The cursor maintains a {@link NativeType} which provides access to the image data.
	 * When the cursor moves from one cell to the next, the underlying data array
	 * of the {@link NativeType} must be switched to the data array of the new cell.
	 * 
	 * <p>
	 * To achieve this, the {@link CellCursor} calls {@link updateContainer()} with
	 * itself as the argument. {@link updateContainer()} in turn will call {@link update()}
	 * on it's container, passing along the reference to the cursor.
	 * In this example, the container would be a {@link CellContainer}.
	 * While the {@link NativeType} does not know about the type of the cursor,
	 * the container does.
	 * {@link CellContainer} knows that it is passed a {@link CellCursor} instance,
	 * which can be used to figure out the current cell and the underlying data array,
	 * which is then returned to the {@link NativeType}.
	 * 
	 * <p>
	 * The idea behind this concept is maybe not obvious. The {@link NativeType} knows
	 * which basic type is used (float, int, byte, ...). However, it does not know how
	 * the data is stored ({@link Array}, {@link CellContainer}, ...).
	 * This prevents the need for multiple implementations of {@link NativeType}.
	 * 
	 * @param c   reference to an accessor which can be passed on to the
	 * 			  container (which will know what to do with it).
	 */
	public void updateContainer( Object c );

	/**
	 * Set the index into the current data array.
	 * 
	 * <p>
	 * This is used by accessors (e.g., a {@link Cursor})
	 * to position the {@link NativeType} in the container. 
	 * 
	 * @param i   the new array index
	 */
	public void updateIndex( final int i );

	/**
	 * Get the current index into the current data array.
	 * 
	 * <p>
	 * This is used by accessors (e.g., a {@link Cursor})
	 * to position the {@link NativeType} in the container. 
	 * 
	 * @return   the current index into the underlying data array
	 */
	public int getIndex();

	/**
	 * Increment the index into the current data array.
	 * 
	 * <p>
	 * This is used by accessors (e.g., a {@link Cursor})
	 * to position the {@link NativeType} in the container. 
	 */
	public void incIndex();

	/**
	 * Increases the index into the current data array by 
	 * {@link increment} steps.
	 * 
	 * <p>
	 * This is used by accessors (e.g., a {@link Cursor})
	 * to position the {@link NativeType} in the container. 
	 * 
	 * @param increment   how many steps
	 */
	public void incIndex( final int increment );

	/**
	 * Decrement the index into the current data array.
	 * 
	 * <p>
	 * This is used by accessors (e.g., a {@link Cursor})
	 * to position the {@link NativeType} in the container. 
	 */
	public void decIndex();

	/**
	 * Decrease the index into the current data array by
	 * {@link decrement} steps.
	 * 
	 * <p>
	 * This is used by accessors (e.g., a {@link Cursor})
	 * to position the {@link NativeType} in the container. 
	 * 
	 * @param decrement   how many steps
	 */
	public void decIndex( final int decrement );
	
}
