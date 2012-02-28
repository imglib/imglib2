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
 * @author Tobias Pietzsch
 */
package net.imglib2.img.sparse;

import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.NativeImg;
import net.imglib2.img.NativeImgFactory;
import net.imglib2.img.basictypeaccess.BitAccess;
import net.imglib2.img.basictypeaccess.ByteAccess;
import net.imglib2.img.basictypeaccess.CharAccess;
import net.imglib2.img.basictypeaccess.DoubleAccess;
import net.imglib2.img.basictypeaccess.FloatAccess;
import net.imglib2.img.basictypeaccess.LongAccess;
import net.imglib2.img.basictypeaccess.ShortAccess;
import net.imglib2.type.NativeType;

/**
 * @author Tobias Pietzsch
 *
 */
public class NtreeImgFactory< T extends NativeType<T> > extends NativeImgFactory< T >
{
	@Override
	public NtreeImg< T > create( final long[] dim, final T type )
	{
		return ( NtreeImg< T > ) type.createSuitableNativeImg( this, dim );
	}

	@Override
	public NativeImg< T, ? extends BitAccess > createBitInstance( final long[] dimensions, final int entitiesPerPixel )
	{
		throw new RuntimeException( "not implemented" );
	}

	@Override
	public NativeImg< T, ? extends ByteAccess > createByteInstance( final long[] dimensions, final int entitiesPerPixel )
	{
		throw new RuntimeException( "not implemented" );
	}

	@Override
	public NativeImg< T, ? extends CharAccess > createCharInstance( final long[] dimensions, final int entitiesPerPixel )
	{
		throw new RuntimeException( "not implemented" );
	}

	@Override
	public NativeImg< T, ? extends ShortAccess > createShortInstance( final long[] dimensions, final int entitiesPerPixel )
	{
		throw new RuntimeException( "not implemented" );
	}

	@Override
	public NtreeImg< T > createIntInstance( final long[] dimensions, final int entitiesPerPixel )
	{
		if( entitiesPerPixel != 1 )
			throw new RuntimeException( "not implemented" );
		return new NtreeImg< T >( dimensions );
	}

	@Override
	public NativeImg< T, ? extends LongAccess > createLongInstance( final long[] dimensions, final int entitiesPerPixel )
	{
		throw new RuntimeException( "not implemented" );
	}

	@Override
	public NativeImg< T, ? extends FloatAccess > createFloatInstance( final long[] dimensions, final int entitiesPerPixel )
	{
		throw new RuntimeException( "not implemented" );
	}

	@Override
	public NativeImg< T, ? extends DoubleAccess > createDoubleInstance( final long[] dimensions, final int entitiesPerPixel )
	{
		throw new RuntimeException( "not implemented yet" );
	}

	@SuppressWarnings( { "unchecked", "rawtypes" } )
	@Override
	public < S > ImgFactory< S > imgFactory( final S type ) throws IncompatibleTypeException
	{
		if ( NativeType.class.isInstance( type ) )
			return new NtreeImgFactory();
		else
			throw new IncompatibleTypeException( this, type.getClass().getCanonicalName() + " does not implement NativeType." );
	}

}
