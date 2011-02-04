/**
 * Copyright (c) 2010, Stephan Saalfeld
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the imglib project nor
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
package mpicbg.imglib.container;

import mpicbg.imglib.InjectiveIntegerInterval;
import mpicbg.imglib.IntegerRandomAccessible;
import mpicbg.imglib.RandomAccessibleIntegerInterval;
import mpicbg.imglib.outofbounds.OutOfBoundsFactory;

/**
 * Containers are {@link InjectiveIntegerInterval} that has its min at
 * 0<sup><em>n</em></sup> and its max positive.  Containers store pixels
 * and thus are the basis for conventional image processing.
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public interface Container<	T >
	extends
		IntegerRandomAccessible< T >,
		RandomAccessibleIntegerInterval< T, Container< T > >,
		InjectiveIntegerInterval
{
	@Override
	public ContainerRandomAccess< T > integerRandomAccess();
	
	@Override
	public ContainerRandomAccess< T > integerRandomAccess( OutOfBoundsFactory< T, Container< T > > factory );
	
	@Override
	public ContainerIterator< T > cursor();

	@Override
	public ContainerIterator< T > localizingCursor();

	/**
	 * Return a {@link ContainerFactory} that creates Containers
	 * of the same kind as this one.
	 * 
	 * This is useful to create containers for temporary storage
	 * in generic methods where the specific container type is
	 * unknown.  Note, that the factory can be used even if
	 * all references to this container have been invalidated. 
	 *  
	 * @return a factory for containers of the same kind as this one. 
	 */
	public ContainerFactory factory();
	
	/**
	 * Create a new variable of the type stored in this container.
	 * 
	 * The variable is useful in generic methods to store temporary
	 * results, e.g., a running sum over pixels in the container.
	 * 
	 * @return a variable of the type contained in this container. 
	 */
	public T createVariable();
	
	public long numPixels();
}
