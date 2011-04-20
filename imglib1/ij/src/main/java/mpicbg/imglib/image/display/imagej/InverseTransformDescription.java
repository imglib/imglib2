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
package mpicbg.imglib.image.display.imagej;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.interpolation.InterpolatorFactory;
import mpicbg.imglib.type.Type;
import mpicbg.models.InvertibleBoundable;

public class InverseTransformDescription<T extends Type<T>> 
{
	final InvertibleBoundable transform;
	final InterpolatorFactory<T> factory;
	final Image<T> image;
	final float[] offset;
	final int numDimensions;
	
	public InverseTransformDescription( final InvertibleBoundable transform, final InterpolatorFactory<T> factory, final Image<T> image )
	{
		this.transform = transform;
		this.factory = factory;
		this.image = image;
		this.numDimensions = image.getNumDimensions();
		this.offset = new float[ numDimensions ];
	}
	
	public InvertibleBoundable getTransform() { return transform; }
	public InterpolatorFactory<T> getInterpolatorFactory() { return factory; }
	public Image<T> getImage() { return image; }
	
	public void setOffset( final float[] offset )
	{
		for ( int d = 0; d < numDimensions; ++d )
			this.offset[ d ] = offset[ d ];
	}
	
	public float[] getOffset() { return offset.clone(); }
	public void getOffset( final float[] offset )
	{
		for ( int d = 0; d < numDimensions; ++d )
			offset[ d ] = this.offset[ d ];		
	}
}
