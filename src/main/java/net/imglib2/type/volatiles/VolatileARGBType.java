/*
 * #%L
 * BigDataViewer core classes with minimal dependencies
 * %%
 * Copyright (C) 2012 - 2016 Tobias Pietzsch, Stephan Saalfeld, Stephan Preibisch,
 * Jean-Yves Tinevez, HongKee Moon, Johannes Schindelin, Curtis Rueden, John Bogovic
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package net.imglib2.type.volatiles;

import net.imglib2.Volatile;
import net.imglib2.img.NativeImg;
import net.imglib2.img.basictypeaccess.IntAccess;
import net.imglib2.img.basictypeaccess.volatiles.VolatileIntAccess;
import net.imglib2.img.basictypeaccess.volatiles.VolatileShortAccess;
import net.imglib2.img.basictypeaccess.volatiles.array.VolatileIntArray;
import net.imglib2.type.NativeTypeFactory;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.integer.UnsignedShortType;

/**
 * A {@link Volatile} variant of {@link UnsignedShortType}. It uses an
 * underlying {@link UnsignedShortType} that maps into a
 * {@link VolatileShortAccess}.
 *
 * @author Stephan Saalfeld &lt;saalfelds@janelia.hhmi.org&gt;
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public class VolatileARGBType extends AbstractVolatileNativeNumericType< ARGBType, VolatileARGBType >
{
	final protected NativeImg< ?, ? extends VolatileIntAccess > img;

	private static class WrappedARGBType extends ARGBType
	{
		public WrappedARGBType( final NativeImg< ?, ? extends IntAccess > img )
		{
			super( img );
		}

		public WrappedARGBType( final IntAccess access )
		{
			super( access );
		}

		public void setAccess( final IntAccess access )
		{
			dataAccess = access;
		}
	}

	// this is the constructor if you want it to read from an array
	public VolatileARGBType( final NativeImg< ?, ? extends VolatileIntAccess > img )
	{
		super( new WrappedARGBType( img ), false );
		this.img = img;
	}

	// this is the constructor if you want to specify the dataAccess
	public VolatileARGBType( final VolatileIntAccess access )
	{
		super( new WrappedARGBType( access ), access.isValid() );
		this.img = null;
	}

	// this is the constructor if you want it to be a variable
	public VolatileARGBType( final int value )
	{
		this( new VolatileIntArray( 1, true ) );
		set( value );
	}

	// this is the constructor if you want it to be a variable
	public VolatileARGBType()
	{
		this( 0 );
	}

	public void set( final int value )
	{
		get().set( value );
	}

	@Override
	public void updateContainer( final Object c )
	{
		final VolatileIntAccess a = img.update( c );
		( ( WrappedARGBType ) t ).setAccess( a );
		setValid( a.isValid() );
	}

	@Override
	public VolatileARGBType duplicateTypeOnSameNativeImg()
	{
		return new VolatileARGBType( img );
	}

	@Override
	public VolatileARGBType createVariable()
	{
		return new VolatileARGBType();
	}

	@Override
	public VolatileARGBType copy()
	{
		final VolatileARGBType v = createVariable();
		v.set( this );
		return v;
	}

	private static final NativeTypeFactory< VolatileARGBType, VolatileIntAccess > typeFactory = NativeTypeFactory.INT( img -> new VolatileARGBType( img ) );

	@Override
	public NativeTypeFactory< VolatileARGBType, ? > getNativeTypeFactory()
	{
		return typeFactory;
	}
}
