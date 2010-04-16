/**
 * Copyright (c) 2009--2010, Stephan Preibisch & Albert Cardona
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
 * @author Stephan Preibisch & Albert Cardona
 */
package mpicbg.imglib.container.shapelist;

import mpicbg.imglib.cursor.shapelist.ShapeListCache;
import mpicbg.imglib.cursor.shapelist.ShapeListCacheFIFO;
import mpicbg.imglib.cursor.shapelist.ShapeListCachedLocalizableByDimCursor;
import mpicbg.imglib.cursor.shapelist.ShapeListCachedLocalizableByDimOutOfBoundsCursor;
import mpicbg.imglib.cursor.shapelist.ShapeListCachedLocalizablePlaneCursor;
import mpicbg.imglib.cursor.shapelist.ShapeListLocalizableByDimCursor;
import mpicbg.imglib.cursor.shapelist.ShapeListLocalizableByDimOutOfBoundsCursor;
import mpicbg.imglib.cursor.shapelist.ShapeListLocalizablePlaneCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.type.Type;

public class ShapeListCached<T extends Type<T> > extends ShapeList<T>
{
	ShapeListCache<T> cache;
		
	public ShapeListCached( final ShapeListContainerFactory factory, final int[] dim, final T background )
	{
		super( factory, dim, background );
		
		this.cache = new ShapeListCacheFIFO<T>( factory.getCacheSize(), this );
	}
	
	public ShapeListCached( final int[] dim, final T background, final int cacheSize )
	{
		this( new ShapeListContainerFactory( cacheSize ), dim, background );
	}

	public ShapeListCached( final int[] dim, final T background )
	{
		this( dim, background, 32 );
	}
	
	public ShapeListCache<T> getShapeListCachingStrategy() { return cache; }
	public void setShapeListCachingStrategy( final ShapeListCache<T> cache ) { this.cache = cache; }

	@Override
	public ShapeListLocalizablePlaneCursor< T > createLocalizablePlaneCursor( final Image< T > image ) 
	{ 
		return new ShapeListCachedLocalizablePlaneCursor< T >( this, image, cache.getCursorCacheInstance() );
	}
	
	@Override
	public ShapeListLocalizableByDimCursor< T > createLocalizableByDimCursor( final Image< T > image ) 
	{
		return new ShapeListCachedLocalizableByDimCursor< T >( this, image, cache.getCursorCacheInstance() );
	}
	
	@Override
	public ShapeListLocalizableByDimOutOfBoundsCursor< T > createLocalizableByDimCursor( final Image< T > image, final OutOfBoundsStrategyFactory< T > outOfBoundsFactory ) 
	{
		return new ShapeListCachedLocalizableByDimOutOfBoundsCursor< T >( this, image, outOfBoundsFactory, cache.getCursorCacheInstance() );
	}
}
