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
