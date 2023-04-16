package net.imglib2.blocks;

import java.util.function.Supplier;
import net.imglib2.converter.Converter;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.FloatType;

/*
TODO: Performance of Convert is very unstable.
      Different Java versions can optimize ConvertGeneric (and modified versions) to different degrees.
      Although actual type in the convert loop are identical, specialized versions (e.g.,
      ConvertImpl.Convert_UnsignedByteType_FloatType) are often twice as fast.
      Also, the only experiments I have run so far are isolated instances with a single converter.
      For real work loads, probably it is required to additionally use ClassCopyProvider, switching on the Converter (supplier) type.
      More experiments are needed.
      See ConvertBenchmark.

TODO: Implement more special case converters.
*/
interface Convert
{
	void convert( Object src, Object dest, final int length );

	Convert newInstance();

	static < A extends NativeType< A >, B extends NativeType< B > > Convert create(
			final A srcType,
			final B destType,
			final Supplier< Converter< A, B > > converterSupplier )
	{
		if ( srcType instanceof UnsignedByteType )
		{
			if ( destType instanceof FloatType )
			{
				return new ConvertImpl.Convert_UnsignedByteType_FloatType( ( Supplier ) converterSupplier );
			}
		}
		else if ( srcType instanceof UnsignedShortType )
		{
			if ( destType instanceof FloatType )
			{
				return new ConvertImpl.Convert_UnsignedShortType_FloatType( ( Supplier ) converterSupplier );
			}
		}

		return new ConvertImpl.ConvertGeneric<>( srcType, destType, converterSupplier );
	}
}
