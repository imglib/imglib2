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
 *
 * @author Stephan Preibisch & Stephan Saalfeld
 */
package mpicbg.imglib.type;

import mpicbg.imglib.container.array.Array;
import mpicbg.imglib.container.basictypecontainer.DataAccess;

/**
 * The {@link Type} class is responsible for computing. It can be instaniated as a variable holding one single value only or with
 * a NativeContainer. There is no differentiation between the two cases except for the constructor to avoid double implementations. 
 * 
 * The {@link Type} is the only class that is aware of the actual data type, i.e. which basic type ({@link DataAccess}) is used to 
 * store the data. On the other hand it does not know the storage type ({@link Array}, {@link Cursor}, ...). This is not necessary for
 * computation and avoid complicated re-implementations. The method public void updateDataArray( Cursor<?> c );	links the NativeContainer and
 * the cursor which define the current position as well as the current storage array.
 * 
 * @author Stephan Preibisch
 *
 * @param <T> - the specialized version
 */
public interface Type<T extends Type<T>>
{	
	/**
	 * Creates a new {@link Type} which can only store one value.
	 * @return - a new {@link Type} instance
	 */
	public T createVariable();
	
	/**
	 * Creates a new {@link Type} which can only store one value but contains the value of this {@link Type}
	 * @return - a new {@link Type} instance
	 */
	public T copy();

	/**
	 * Sets the value of another {@link Type}.
	 * @param c - the new value
	 */
	public void set( T c );		
}
