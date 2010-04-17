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
package mpicbg.imglib.sampler;

import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.interpolation.Interpolator;
import mpicbg.imglib.type.Type;

/** 
 * The {@link Sampler} interface provides access to a {@link Type} instance.
 * This {@link Type} instance may point to an actual pixel stored in a
 * {@link Container} or be generated differently.
 * 
 * The {@link Sampler} interface unifies pixel access for {@link Cursor},
 * {@link Interpolator} and later Integrators.   and Interpolators are actually
point samplers, whereas many operations require
integrating a region defined by some function.  The
Sampler interface is the basis of 
 *  
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 * 
 */
public interface Sampler< T extends Type< T > >
{
	/**
	 * Access the actual {@link Type} instance providing access to a pixel,
	 * sub-pixel or integral region value the {@link Sampler} points at.
	 * 
	 * @return
	 */
	public T type();
	
	@Deprecated
	public T getType();
}
