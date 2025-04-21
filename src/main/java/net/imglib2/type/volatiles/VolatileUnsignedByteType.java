/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2025 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
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
import net.imglib2.img.basictypeaccess.ByteAccess;
import net.imglib2.img.basictypeaccess.volatiles.VolatileByteAccess;
import net.imglib2.img.basictypeaccess.volatiles.array.VolatileByteArray;
import net.imglib2.type.NativeTypeFactory;
import net.imglib2.type.numeric.integer.UnsignedByteType;

/**
 * A {@link Volatile} variant of {@link UnsignedByteType}. It uses an
 * underlying {@link UnsignedByteType} that maps into a
 * {@link VolatileByteAccess}.
 *
 * @author Stephan Saalfeld &lt;saalfelds@janelia.hhmi.org&gt;
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public class VolatileUnsignedByteType extends AbstractVolatileNativeRealType< UnsignedByteType, VolatileUnsignedByteType >
{
	final protected NativeImg< ?, ? extends VolatileByteAccess > img;

	private static class WrappedUnsignedByteType extends UnsignedByteType
	{
		public WrappedUnsignedByteType( final NativeImg<?, ? extends ByteAccess> img )
		{
			super( img );
		}

		public WrappedUnsignedByteType( final ByteAccess access )
		{
			super( access );
		}

		public void setAccess( final ByteAccess access )
		{
			dataAccess = access;
		}
	}

	// this is the constructor if you want it to read from an array
	public VolatileUnsignedByteType( final NativeImg< ?, ? extends VolatileByteAccess > img )
	{
		super( new WrappedUnsignedByteType( img ), false );
		this.img = img;
	}

	// this is the constructor if you want to specify the dataAccess
	public VolatileUnsignedByteType( final VolatileByteAccess access )
	{
		super( new WrappedUnsignedByteType( access ), access.isValid() );
		this.img = null;
	}

	// this is the constructor if you want it to be a variable
	public VolatileUnsignedByteType( final int value )
	{
		this( new VolatileByteArray( 1, true ) );
		set( value );
	}

	// this is the constructor if you want it to be a variable
	public VolatileUnsignedByteType()
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
		final VolatileByteAccess a = img.update( c );
		( ( WrappedUnsignedByteType )t ).setAccess( a );
		setValid( a.isValid() );
	}

	@Override
	public VolatileUnsignedByteType duplicateTypeOnSameNativeImg()
	{
		return new VolatileUnsignedByteType( img );
	}

	@Override
	public VolatileUnsignedByteType createVariable()
	{
		return new VolatileUnsignedByteType();
	}

	@Override
	public VolatileUnsignedByteType copy()
	{
		final VolatileUnsignedByteType v = createVariable();
		v.set( this );
		return v;
	}

	private static final NativeTypeFactory< VolatileUnsignedByteType, VolatileByteAccess > typeFactory = NativeTypeFactory.BYTE( VolatileUnsignedByteType::new );

	@Override
	public NativeTypeFactory< VolatileUnsignedByteType, ? > getNativeTypeFactory()
	{
		return typeFactory;
	}
}
