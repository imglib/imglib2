/*-
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
package net.imglib2.blocks;

import java.util.function.Function;
import java.util.function.Supplier;
import net.imglib2.converter.Converter;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.img.basictypeaccess.array.ShortArray;
import net.imglib2.type.NativeType;
import net.imglib2.type.NativeTypeFactory;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.FloatType;

class ConvertImpl
{
	static class Convert_UnsignedShortType_FloatType implements Convert
	{
		private final Supplier< Converter< UnsignedShortType, FloatType > > converterSupplier;

		private final Converter< UnsignedShortType, FloatType > converter;

		public Convert_UnsignedShortType_FloatType(final Supplier< Converter< UnsignedShortType, FloatType > > converterSupplier)
		{
			this.converterSupplier = converterSupplier;
			converter = converterSupplier.get();
		}

		@Override
		public void convert( final Object src, final Object dest, final int length )
		{
			final UnsignedShortType in = new UnsignedShortType( new ShortArray( ( short[] ) src ) );
			final FloatType out = new FloatType( new FloatArray( ( float[] ) dest ) );
			for ( int i = 0; i < length; i++ )
			{
				in.index().set( i );
				out.index().set( i );
				converter.convert( in, out );
			}
		}

		// creates an independent copy of {@code convert}
		private Convert_UnsignedShortType_FloatType( Convert_UnsignedShortType_FloatType convert )
		{
			converterSupplier = convert.converterSupplier;
			converter = converterSupplier.get();
		}

		@Override
		public Convert newInstance()
		{
			return new Convert_UnsignedShortType_FloatType( this );
		}
	}

	static class Convert_UnsignedByteType_FloatType implements Convert
	{
		private final Supplier< Converter< UnsignedByteType, FloatType > > converterSupplier;

		private final Converter< UnsignedByteType, FloatType > converter;

		public Convert_UnsignedByteType_FloatType(final Supplier< Converter< UnsignedByteType, FloatType > > converterSupplier)
		{
			this.converterSupplier = converterSupplier;
			converter = converterSupplier.get();
		}

		@Override
		public void convert( final Object src, final Object dest, final int length )
		{
			final UnsignedByteType in = new UnsignedByteType( new ByteArray( ( byte[] ) src ) );
			final FloatType out = new FloatType( new FloatArray( ( float[] ) dest ) );
			for ( int i = 0; i < length; i++ )
			{
				in.index().set( i );
				out.index().set( i );
				converter.convert( in, out );
			}
		}

		// creates an independent copy of {@code convert}
		private Convert_UnsignedByteType_FloatType( Convert_UnsignedByteType_FloatType convert )
		{
			converterSupplier = convert.converterSupplier;
			converter = converterSupplier.get();
		}

		@Override
		public Convert newInstance()
		{
			return new Convert_UnsignedByteType_FloatType( this );
		}
	}

	static class ConvertGeneric< A extends NativeType< A >, B extends NativeType< B > > implements Convert
	{
		private final Supplier< Converter< A, B > > converterSupplier;

		private final Converter< A, B > converter;

		private final Function< Object, A > srcWrapper;

		private final Function< Object, B > destWrapper;

		public ConvertGeneric( final A srcType, final B destType, final Supplier< Converter< A, B > > converterSupplier )
		{
			this.converterSupplier = converterSupplier;
			converter = converterSupplier.get();
			srcWrapper = wrapperForType( srcType );
			destWrapper = wrapperForType( destType );
		}

		@Override
		public void convert( Object src, Object dest, final int length )
		{
			A in = srcWrapper.apply( src );
			B out = destWrapper.apply( dest );
			for ( int i = 0; i < length; i++ )
			{
				in.index().set( i );
				out.index().set( i );
				converter.convert( in, out );
			}
		}

		// creates an independent copy of {@code convert}
		private ConvertGeneric( ConvertGeneric< A, B > convert )
		{
			converterSupplier = convert.converterSupplier;
			converter = converterSupplier.get();
			srcWrapper = convert.srcWrapper;
			destWrapper = convert.destWrapper;
		}

		@Override
		public Convert newInstance()
		{
			return new ConvertGeneric<>( this );
		}

		static < T extends NativeType< T >, A extends ArrayDataAccess< A > > Function< Object, T > wrapperForType( T type )
		{
			final NativeTypeFactory< T, A > nativeTypeFactory = ( NativeTypeFactory< T, A > ) type.getNativeTypeFactory();
			final PrimitiveTypeProperties< ?, A > props = ( PrimitiveTypeProperties< ?, A > ) PrimitiveTypeProperties.get( nativeTypeFactory.getPrimitiveType() );
			return array -> {
				final ArrayImg< T, A > img = new ArrayImg<>( props.wrap( array ), new long[] { 1 }, type.getEntitiesPerPixel() );
				final T t = nativeTypeFactory.createLinkedType( img );
				t.updateContainer( null );
				return t;
			};
		}
	}
}
