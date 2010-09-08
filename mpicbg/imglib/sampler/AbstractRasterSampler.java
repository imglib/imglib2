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
package mpicbg.imglib.sampler;

import mpicbg.imglib.container.Container;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.Type;

/**
 * We use the class {@link AbstractRasterSampler} instead of implementing methods here so that other classes can
 * only implement {@link RasterIterator} and extend other classes instead. As each {@link AbstractRasterSampler} is also
 * a {@link RasterIterator} there are no disadvantages for the {@link RasterIterator} implementations.
 * 
 * @author Stephan Preibisch and Stephan Saalfeld
 *
 * @param < T > the {@link Type} to be returned by {@link #type()}
 */
public abstract class AbstractRasterSampler< T extends Type< T > > implements RasterSampler< T >
{
	/* the image whose pixels this AbstractCursor is accessing, can be null */
	final protected Image< T > image;

	/* a copy of container.numDimensions() for slightly faster access */
	final protected int numDimensions;
	
	public AbstractRasterSampler( final Container< T > container, final Image< T > image )
	{
		this.image = image;
		numDimensions = container.numDimensions();
	}

	@Override
	@Deprecated
	final public T getType(){ return type(); } 
	
	@Override
	public int getArrayIndex() { return type().getIndex(); }
	
	@Override
	public Image<T> getImage() { return image; }
	
	@Override
	public void close()
	{
		if ( image != null)
			image.removeRasterSampler( this ); 
	}
	
	@Override
	public int numDimensions(){ return numDimensions; }
}
