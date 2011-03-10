/**
 * Copyright (c) 2009--2010, Albert Cardona, Stephan Preibisch & Stephan Saalfeld
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
package mpicbg.imglib.img.shapelist;

import mpicbg.imglib.img.AbstractImgOutOfBoundsRandomAccess;
import mpicbg.imglib.img.Img;
import mpicbg.imglib.img.ImgCursor;
import mpicbg.imglib.img.ImgRandomAccess;
import mpicbg.imglib.outofbounds.OutOfBoundsFactory;
import mpicbg.imglib.type.Type;

/**
 * 
 * @param <T>
 *
 * @author Albert Cardona, Stephan Preibisch and Stephan Saalfeld
 */
public class ShapeListCached< T extends Type< T > > extends ShapeList< T >
{
	ShapeListCache< T > cache;
	static public final int DEFAULT_CACHE_SIZE = 32;

	public ShapeListCached( final long[] dim, final T background )
	{
		this( dim, background, DEFAULT_CACHE_SIZE );
	}
	
	public ShapeListCached( final long[] dim, final T background, final int cacheSize )
	{
		super( dim, background );

		this.cache = new ShapeListCacheFIFO< T >( cacheSize, this );
	}

	public ShapeListCache< T > getShapeListCachingStrategy()
	{
		return cache;
	}

	public void setShapeListCachingStrategy( final ShapeListCache< T > cache )
	{
		this.cache = cache;
	}
	
	@Override
	public ImgRandomAccess<T> randomAccess() {
		return new ShapeListCachedPositionableRasterSampler< T >( this );
	}

	@Override
	public AbstractImgOutOfBoundsRandomAccess<T> randomAccess( final OutOfBoundsFactory< T, Img<T> > factory ) {
		return new AbstractImgOutOfBoundsRandomAccess< T >( this, factory );
	}

	@Override
	public ImgCursor<T> cursor() {
		return new ShapeListCachedPositionableRasterSampler< T >( this );
	}
}
