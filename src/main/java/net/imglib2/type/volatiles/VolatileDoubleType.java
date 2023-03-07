/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2023 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
import net.imglib2.img.basictypeaccess.DoubleAccess;
import net.imglib2.img.basictypeaccess.volatiles.VolatileDoubleAccess;
import net.imglib2.img.basictypeaccess.volatiles.array.VolatileDoubleArray;
import net.imglib2.type.NativeTypeFactory;
import net.imglib2.type.numeric.real.DoubleType;

/**
 * A {@link Volatile} variant of {@link DoubleType}. It uses an
 * underlying {@link DoubleType} that maps into a
 * {@link VolatileDoubleAccess}.
 *
 * @author Stephan Saalfeld
 */
public class VolatileDoubleType extends AbstractVolatileNativeRealType< DoubleType, VolatileDoubleType >
{
	final protected NativeImg< ?, ? extends VolatileDoubleAccess > img;

	private static class WrappedDoubleType extends DoubleType
	{
		public WrappedDoubleType( final NativeImg<?, ? extends DoubleAccess> img )
		{
			super( img );
		}

		public WrappedDoubleType( final DoubleAccess access )
		{
			super( access );
		}

		public void setAccess( final DoubleAccess access )
		{
			dataAccess = access;
		}
	}

	// this is the constructor if you want it to read from an array
	public VolatileDoubleType( final NativeImg< ?, ? extends VolatileDoubleAccess > img )
	{
		super( new WrappedDoubleType( img ), false );
		this.img = img;
	}

	// this is the constructor if you want to specify the dataAccess
	public VolatileDoubleType( final VolatileDoubleAccess access )
	{
		super( new WrappedDoubleType( access ), access.isValid() );
		this.img = null;
	}

	// this is the constructor if you want it to be a variable
	public VolatileDoubleType( final double value )
	{
		this( new VolatileDoubleArray( 1, true ) );
		set( value );
	}

	// this is the constructor if you want it to be a variable
	public VolatileDoubleType()
	{
		this( 0 );
	}

	public void set( final double value )
	{
		get().set( value );
	}

	@Override
	public void updateContainer( final Object c )
	{
		final VolatileDoubleAccess a = img.update( c );
		( ( WrappedDoubleType )t ).setAccess( a );
		setValid( a.isValid() );
	}

	@Override
	public VolatileDoubleType duplicateTypeOnSameNativeImg()
	{
		return new VolatileDoubleType( img );
	}

	@Override
	public VolatileDoubleType createVariable()
	{
		return new VolatileDoubleType();
	}

	@Override
	public VolatileDoubleType copy()
	{
		final VolatileDoubleType v = createVariable();
		v.set( this );
		return v;
	}

	private static final NativeTypeFactory< VolatileDoubleType, VolatileDoubleAccess > typeFactory = NativeTypeFactory.DOUBLE( VolatileDoubleType::new );

	@Override
	public NativeTypeFactory< VolatileDoubleType, ? > getNativeTypeFactory()
	{
		return typeFactory;
	}
}
