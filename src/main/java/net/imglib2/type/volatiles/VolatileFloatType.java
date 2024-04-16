/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2024 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
import net.imglib2.img.basictypeaccess.FloatAccess;
import net.imglib2.img.basictypeaccess.volatiles.VolatileFloatAccess;
import net.imglib2.img.basictypeaccess.volatiles.array.VolatileFloatArray;
import net.imglib2.type.NativeTypeFactory;
import net.imglib2.type.numeric.real.FloatType;

/**
 * A {@link Volatile} variant of {@link FloatType}. It uses an
 * underlying {@link FloatType} that maps into a
 * {@link VolatileFloatAccess}.
 *
 * @author Stephan Saalfeld
 */
public class VolatileFloatType extends AbstractVolatileNativeRealType< FloatType, VolatileFloatType >
{
	final protected NativeImg< ?, ? extends VolatileFloatAccess > img;

	private static class WrappedFloatType extends FloatType
	{
		public WrappedFloatType( final NativeImg<?, ? extends FloatAccess> img )
		{
			super( img );
		}

		public WrappedFloatType( final FloatAccess access )
		{
			super( access );
		}

		public void setAccess( final FloatAccess access )
		{
			dataAccess = access;
		}
	}

	// this is the constructor if you want it to read from an array
	public VolatileFloatType( final NativeImg< ?, ? extends VolatileFloatAccess > img )
	{
		super( new WrappedFloatType( img ), false );
		this.img = img;
	}

	// this is the constructor if you want to specify the dataAccess
	public VolatileFloatType( final VolatileFloatAccess access )
	{
		super( new WrappedFloatType( access ), access.isValid() );
		this.img = null;
	}

	// this is the constructor if you want it to be a variable
	public VolatileFloatType( final float value )
	{
		this( new VolatileFloatArray( 1, true ) );
		set( value );
	}

	// this is the constructor if you want it to be a variable
	public VolatileFloatType()
	{
		this( 0 );
	}

	public void set( final float value )
	{
		get().set( value );
	}

	@Override
	public void updateContainer( final Object c )
	{
		final VolatileFloatAccess a = img.update( c );
		( ( WrappedFloatType )t ).setAccess( a );
		setValid( a.isValid() );
	}

	@Override
	public VolatileFloatType duplicateTypeOnSameNativeImg()
	{
		return new VolatileFloatType( img );
	}

	@Override
	public VolatileFloatType createVariable()
	{
		return new VolatileFloatType();
	}

	@Override
	public VolatileFloatType copy()
	{
		final VolatileFloatType v = createVariable();
		v.set( this );
		return v;
	}

	private static final NativeTypeFactory< VolatileFloatType, VolatileFloatAccess > typeFactory = NativeTypeFactory.FLOAT( VolatileFloatType::new );

	@Override
	public NativeTypeFactory< VolatileFloatType, ? > getNativeTypeFactory()
	{
		return typeFactory;
	}
}
