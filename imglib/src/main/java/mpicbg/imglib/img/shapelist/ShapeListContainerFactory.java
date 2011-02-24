/**
 * Copyright (c) 2010, Stephan Saalfeld & Stephan Preibisch & Albert Cardona
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

import mpicbg.imglib.exception.IncompatibleTypeException;
import mpicbg.imglib.img.Img;
import mpicbg.imglib.img.ImgFactory;
import mpicbg.imglib.img.NativeImgFactory;
import mpicbg.imglib.type.Type;

public class ShapeListContainerFactory< T extends Type< T > > extends ImgFactory< T >
{
	boolean useCaching = false;
	int cacheSize = ShapeListCached.DEFAULT_CACHE_SIZE;
	
	public ShapeListContainerFactory() {}
	public ShapeListContainerFactory( final int cacheSize )
	{
		this.useCaching = true;
		this.cacheSize = cacheSize;
	}
	
	public void setCaching( final boolean useCaching ) { this.useCaching = useCaching; }
	public void setCacheSize( final int cacheSize ) { this.cacheSize = cacheSize; }

	public boolean getCaching() { return useCaching; }
	public int getCacheSize() { return cacheSize; }

	/**
	 * This method is called by {@link Image}. The {@link ImgFactory} can decide how to create the {@link Img},
	 * if it is for example a {@link NativeImgFactory} it will ask the {@link Type} to create a 
	 * suitable {@link Img} for the {@link Type} and the dimensionality
	 * 
	 * @return {@link Img} - the instantiated Container
	 */
	@Override
	public ShapeList<T> create( final long[] dim, final T type )
	{
		if ( useCaching )
			return new ShapeListCached<T>( dim, type );
		else
			return new ShapeList<T>( dim, type );
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public < S > ImgFactory< S > imgFactory(final S type )
			throws IncompatibleTypeException {
		if ( Type.class.isInstance( type ) )
			return new ShapeListContainerFactory();
		throw new IncompatibleTypeException( this, type.getClass().getCanonicalName() + " does not implement Type." );
	}
}
