/**
 * Copyright (c) 2009--2011, Stephan Preibisch & Stephan Saalfeld
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
package net.imglib2;

import net.imglib2.img.Img;
import net.imglib2.type.Type;

/** 
 * <p>The {@link Sampler} interface provides access to a {@link Type} instance.
 * This {@link Type} instance may point to an actual pixel stored in a
 * {@link Img} or be generated differently.</p>
 * 
 * <p>The {@link Sampler} interface unifies pixel access for {@link Iterator},
 * {@link PositionableRasterSampler random access samplers},
 * {@link Interpolator} which are point {@link Sampler Samplers}.</p>
 * 
 * <p>In addition, many operations require integrating a region in a
 * functionally defined way.  The {@link Sampler} interface is the basis of all
 * these strategies.</p>
 *  
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public interface Sampler< T >
{
	/**
	 * Access the actual <em>T</em> instance providing access to a pixel,
	 * sub-pixel or integral region value the {@link Sampler} points at.
	 */
	public T get();
	
	/**
	 * @return - A new {@link Sampler} in the same state accessing the 
	 * same values.
	 *  
	 * It does NOT copy the T, just the state of the {@link Sampler}.
	 * Otherwise use T.copy() if available.
	 * 
	 * Sampler.copy().get() == Sampler.get(), i.e. both hold the same value,
	 * not necessarily the same instance (this is the case for an 
	 * {@link ArrayCursor} for example)
	 */
	public Sampler< T > copy();
}
