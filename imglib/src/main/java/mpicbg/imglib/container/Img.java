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

import mpicbg.imglib.InjectiveInterval;
import mpicbg.imglib.RandomAccessible;
import mpicbg.imglib.RandomAccessibleInterval;
import mpicbg.imglib.outofbounds.OutOfBoundsFactory;

/**
 * {@link Img}s are {@link InjectiveInterval} that has its min at
 * 0<sup><em>n</em></sup> and its max positive.  {@link Img}s store pixels
 * and thus are the basis for conventional image processing.
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public interface Img<	T >
	extends
		RandomAccessible< T >,
		RandomAccessibleInterval< T, Img< T > >,
		InjectiveInterval
{
	@Override
	public ImgRandomAccess< T > randomAccess();
	
	@Override
	public ImgRandomAccess< T > randomAccess( OutOfBoundsFactory< T, Img< T > > factory );
	
	@Override
	public ImgCursor< T > cursor();

	@Override
	public ImgCursor< T > localizingCursor();

	/**
	 * Get a {@link ImgFactory} that creates {@link Img}s
	 * of the same kind as this one.
	 * 
	 * This is useful to create Imgs for temporary storage
	 * in generic methods where the specific Img type is
	 * unknown.  Note, that the factory can be used even if
	 * all references to this Img have been invalidated. 
	 *  
	 * @return a factory for Imgs of the same kind as this one. 
	 */
	public ImgFactory< T > factory();
	
	public long numPixels();
}
