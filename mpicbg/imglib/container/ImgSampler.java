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
package mpicbg.imglib.container;

import mpicbg.imglib.EuclideanSpace;
import mpicbg.imglib.InjectiveIntegerInterval;
import mpicbg.imglib.Sampler;
import mpicbg.imglib.type.Type;

/**
 * {@link ImgSampler} provides access to a `pixel' value in discrete image
 * space.  The step-size in any dimension of the image is 1 raster step which
 * does not necessarily reflect any meaningful analogy in some physical space.
 * For {@link Img Containers} that store actual sample values for each
 * pixel, this interface provides access to these pixel samples
 * 
 * {@link ImgSampler} is the common basic interface to access pixel data in
 * any {@link Img}.  Other {@link Sampler Samplers} build on top of it.
 * 
 * @param <T> the {@link Type} of pixels in the {@link Img}
 * 
 * @author Stephan Preibisch & Stephan Saalfeld
 */
public interface ImgSampler< T > extends Sampler< T >, EuclideanSpace, InjectiveIntegerInterval
{	
	public Img< T > getImg();
}
