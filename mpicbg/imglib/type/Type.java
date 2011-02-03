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

import mpicbg.imglib.container.ContainerIterator;
import mpicbg.imglib.container.DirectAccessContainer;
import mpicbg.imglib.container.DirectAccessContainerFactory;
import mpicbg.imglib.container.array.Array;
import mpicbg.imglib.container.array.ArrayIterator;
import mpicbg.imglib.container.basictypecontainer.DataAccess;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.display.Display;
import mpicbg.imglib.sampler.cell.CellBasicRasterIterator;
import mpicbg.imglib.type.numeric.real.FloatType;

/**
 * The {@link Type} class is responsible for computing. It can be instaniated as
 * a variable holding one single value only or with a DirectAccessContainer.
 * There is no differentiation between the two cases except for the constructor
 * to avoid double implementations.
 * 
 * The {@link Type} is the only class that is aware of the actual data type,
 * i.e. which basic type ({@link DataAccess}) is used to store the data. On the
 * other hand it does not know the storage type ({@link Array},
 * {@link ContainerIterator}, ...). This is not necessary for computation and avoid
 * complicated re-implementations. The method public void updateDataArray(
 * Cursor<?> c ); links the DirectAccessContainer and the cursor which define
 * the current position as well as the current storage array.
 * 
 * @author Stephan Preibisch and Stephan Saalfeld
 * 
 * @param <T> the specialized version
 */
public interface Type< T extends Type< T > >
{
	/**
	 * The {@link Type} creates the DirectAccessContainer used for storing image
	 * data; based on the given storage strategy and its size. It basically only
	 * decides here which BasicType it uses (float, int, byte, bit, ...) and how
	 * many entities per pixel it needs (e.g. 2 floats per pixel for a complex
	 * number). This enables the separation of {@link Image} and the basic
	 * types.
	 * 
	 * @param storageFactory
	 *            - Which storage strategy is used
	 * @param dim
	 *            - the dimensions
	 * @return - the instantiated DirectAccessContainer where only the
	 *         {@link Type} knowns the BasicType it contains.
	 */
	public DirectAccessContainer< T, ? > createSuitableDirectAccessContainer( final DirectAccessContainerFactory storageFactory, final long[] dim );

	/**
	 * The {@link Type} creates the default {@link Display} for displaying the
	 * image contents. Only {@link Type} can do this as in {@link Image} the
	 * {@link Type} is only a Generic. Later the user can create its own
	 * {@link Display}s if wanted.
	 * 
	 * This could be basically a static function.
	 * 
	 * @param image
	 *            - the image to create the {@link Display} for
	 * @return the {@link Display}
	 */
	public Display< T > getDefaultDisplay( Image< T > image );

	/**
	 * This method is used by the {@link ContainerIterator}s to update the data
	 * current data array of the {@link Type}, for example when moving from one
	 * {@link Cell} to the next. If it is only an {@link Array} the
	 * {@link ContainerIterator}s never have to call that function.
	 * 
	 * The idea behind this concept is maybe not obvious. The {@link Type} knows
	 * which basic type is used (float, int, byte, ...) but does not know how it
	 * is stored ({@link Array}, {@link CellDirectAccessContainer}, ...) to
	 * prevent multiple implementations of {@link Type}. That's why {@link Type}
	 * asks the {@link DataAccess} to give the actual basic array by passing the
	 * {@link ContainerIterator} that calls the method. The {@link DataAccess} is
	 * also an {@link Array}, {@link CellDirectAccessContainer}, ... which can
	 * then communicate with the {@link ArrayIterator},
	 * {@link CellBasicRasterIterator}, ... and return the current basic type
	 * array.
	 * 
	 * A typical implementation of this method looks like that (this is the
	 * {@link FloatType} implementation):
	 * 
	 * float[] v = floatStorage.getCurrentStorageArray( c );
	 * 
	 * @param c
	 *            - the {@link ContainerIterator} gives a link to itself so that
	 *            the {@link Type} tell its {@link DataAccess} to get the new
	 *            basic type array.
	 */
	public void updateContainer( Object c );

	/**
	 * Increments the array position of the {@link Type}, this is called by the
	 * {@link ContainerIterator}s which iterate over the image.
	 * 
	 * @param i
	 *            - how many steps
	 */
	public void updateIndex( final int i );

	/**
	 * Returns the current index in the storage array, this is called by the
	 * {@link ContainerIterator}s which iterate over the image.
	 * 
	 * @return - int index
	 */
	public int getIndex();

	/**
	 * Increases the array index, this is called by the {@link ContainerIterator}s
	 * which iterate over the image.
	 */
	public void incIndex();

	/**
	 * Increases the index by increment steps, this is called by the
	 * {@link ContainerIterator}s which iterate over the image.
	 * 
	 * @param increment
	 *            - how many steps
	 */
	public void incIndex( final int increment );

	/**
	 * Decreases the array index, this is called by the {@link ContainerIterator}s
	 * which iterate over the image.
	 */
	public void decIndex();

	/**
	 * Decreases the index by increment steps, this is called by the
	 * {@link ContainerIterator}s which iterate over the image.
	 * 
	 * @param increment
	 *            - how many steps
	 */
	public void decIndex( final int decrement );

	/**
	 * Creates a new {@link Type} which can only store one value.
	 * 
	 * @return - a new {@link Type} instance
	 */
	public T createVariable();

	/**
	 * Creates a new {@link Type} which can only store one value but contains
	 * the value of this {@link Type}
	 * 
	 * @return - a new {@link Type} instance
	 */
	public T clone();

	/**
	 * Creates a new {@link Type} which stores in the same physical array. This
	 * is only used internally.
	 * 
	 * @return - a new {@link Type} instance working on the same
	 *         {@link DirectAccessContainer}
	 */
	public T duplicateTypeOnSameDirectAccessContainer();

	/**
	 * Sets the value of another {@link Type}.
	 * 
	 * @param c
	 *            - the new value
	 */
	public void set( T c );

	/**
	 * Creates a 1d array of the generic {@link Type}
	 * 
	 * @param size1
	 *            - the size of the array
	 * @return - T[] array
	 */
	public T[] createArray1D( int size1 );

	/**
	 * Creates a 2d array of the generic {@link Type}
	 * 
	 * @param size1
	 *            - the size of the array
	 * @param size2
	 *            - the size of the array
	 * @return - T[][] array
	 */
	public T[][] createArray2D( int size1, int size2 );

	/**
	 * Creates a 3d array of the generic {@link Type}
	 * 
	 * @param size1
	 *            - the size of the array
	 * @param size2
	 *            - the size of the array
	 * @param size3
	 *            - the size of the array
	 * @return - T[][][] array
	 */
	public T[][][] createArray3D( int size1, int size2, int size3 );
}
