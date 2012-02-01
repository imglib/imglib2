/**
 * Copyright (c) 2009--2012, ImgLib2 developers
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
package net.imglib2.interpolation.randomaccess;

import net.imglib2.RandomAccessible;
import net.imglib2.RealInterval;
import net.imglib2.interpolation.InterpolatorFactory;
import net.imglib2.type.numeric.RealType;

public class LanczosInterpolatorFactory<T extends RealType<T>> implements InterpolatorFactory< T, RandomAccessible< T > >
{
	int alpha;
	boolean clipping;
	
	/**
	 * Creates a new {@link LanczosInterpolatorFactory} using the Lanczos (sinc) interpolation in a certain window
	 * 
	 * @param alpha - the rectangular radius of the window for perfoming the lanczos interpolation
	 * @param clipping - the lanczos-interpolation can create values that are bigger or smaller than the original values,
	 *        so they can be clipped to the range of the {@link Type} if wanted
	 */
	public LanczosInterpolatorFactory( final int alpha, final boolean clipping )
	{
		this.alpha = alpha;
		this.clipping = clipping;
	}

	/**
	 * Creates a new {@link LanczosInterpolatorFactory} with standard parameters (do clipping, alpha=3)
	 */
	public LanczosInterpolatorFactory()
	{
		this( 3, true );
	}
	
	@Override
	public LanczosInterpolator< T > create( final RandomAccessible< T > randomAccessible )
	{
		return new LanczosInterpolator< T >( randomAccessible, alpha, clipping );
	}

	/**
	 * For now, ignore the {@link RealInterval} and return
	 * {@link #create(RandomAccessible)}.
	 */
	@Override
	public LanczosInterpolator< T > create( final RandomAccessible< T > randomAccessible, final RealInterval interval )
	{
		return create( randomAccessible );
	}

	/**
	 * Set the rectangular radius of the window for perfoming the lanczos interpolation
	 * @param alpha - radius
	 */
	public void setAlpha( final int alpha ) { this.alpha = alpha; }
	
	/**
	 * The lanczos-interpolation can create values that are bigger or smaller than the original values,
	 * so they can be clipped to the range of the {@link RealType} if wanted
	 * 
	 * @param clipping - perform clipping (true)
	 */
	public void setClipping( final boolean clipping ) { this.clipping = clipping; }
	
	/**
	 * @return - rectangular radius of the window for perfoming the lanczos interpolation 
	 */
	public int getAlpha() { return alpha; }
	
	/**
	 * @return - if clipping to the {@link RealType} range will be performed 
	 */
	public boolean getClipping() { return clipping; }
}
