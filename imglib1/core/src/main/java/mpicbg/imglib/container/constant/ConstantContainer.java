/**
 * Copyright (c) 2009--2012, Tobias Pietzsch, Stephan Preibisch & Stephan Saalfeld
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
package mpicbg.imglib.container.constant;

import mpicbg.imglib.container.Container;
import mpicbg.imglib.container.ContainerFactory;
import mpicbg.imglib.container.ContainerImpl;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.cursor.LocalizablePlaneCursor;
import mpicbg.imglib.cursor.constant.ConstantCursor;
import mpicbg.imglib.cursor.constant.ConstantLocalizableByDimCursor;
import mpicbg.imglib.cursor.constant.ConstantLocalizableByDimOutOfBoundsCursor;
import mpicbg.imglib.cursor.constant.ConstantLocalizableCursor;
import mpicbg.imglib.cursor.constant.ConstantLocalizablePlaneCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.type.Type;

/**
 * A simple container that has only one value and returns it at each location.
 * 
 * @author Stephan Preibisch
 *
 * @param <T>
 */
public class ConstantContainer< T extends Type< T > > extends ContainerImpl< T >
{
	final T type;
	ConstantContainerFactory factory;
	
	/**
	 * 
	 * @param factory
	 * @param dim
	 * @param type
	 */
	public ConstantContainer( final ConstantContainerFactory factory, final int[] dim, final T type ) 
	{
		super( factory, dim );
		this.factory = factory;
		this.type = type;
	}

	public ConstantContainer( final int[] dim, final T type ) 
	{
		super( null, dim );
		
		this.factory = null;
		this.type = type;
	}

	@Override
	public ContainerFactory getFactory() 
	{ 
		if ( factory == null )
			this.factory = new ConstantContainerFactory();

		return this.factory; 
	}
	
	@Override
	public ConstantCursor<T> createCursor( final Image<T> image ) { return new ConstantCursor< T >( this, image, type ); }

	@Override
	public LocalizableCursor<T> createLocalizableCursor( final Image<T> image ) { return new ConstantLocalizableCursor< T >( this, image, type ); }

	@Override
	public LocalizablePlaneCursor<T> createLocalizablePlaneCursor( final Image<T> image ) { return new ConstantLocalizablePlaneCursor< T >( this, image, type ); }

	@Override
	public LocalizableByDimCursor<T> createLocalizableByDimCursor(Image<T> image) { return new ConstantLocalizableByDimCursor< T >( this, image, type); }

	@Override
	public LocalizableByDimCursor<T> createLocalizableByDimCursor( final Image<T> image, final OutOfBoundsStrategyFactory<T> outOfBoundsFactory )
	{
		return new ConstantLocalizableByDimOutOfBoundsCursor< T >( this, image, type, outOfBoundsFactory );
	}

	@Override
	public void close() {}

	@Override
	public boolean compareStorageContainerCompatibility( Container<?> img ) { return false; }


}
