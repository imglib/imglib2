package net.imglib2.type.numeric.integer;

import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.IntegerType;

public class IntegerTypes
{
	private IntegerTypes()
	{
		// NB: prevent instantiation of static utility class
	}

	public static IntegerType< ? > smallestType( boolean signed, long max )
	{
		return smallestType( signed ? -1 : 0, max );
	}

	public static IntegerType< ? > smallestType( long min, long max )
	{
		if ( min > max )
			throw new IllegalArgumentException( "Wrong usage: min (" + min + ") > max (" + max + ")" );
		if ( min >= 0 )
		{
			if ( max <= 1L )
				return new BitType();
			if ( max <= 0xffL )
				return new UnsignedByteType();
			if ( max <= 0xffffL )
				return new UnsignedShortType();
			if ( max <= 0xffffffffL )
				return new UnsignedIntType();
			return new UnsignedLongType();
		}
		if ( min >= Byte.MIN_VALUE && max <= Byte.MAX_VALUE )
			return new ByteType();
		if ( min >= Short.MIN_VALUE && max <= Short.MAX_VALUE )
			return new ShortType();
		if ( min >= Integer.MIN_VALUE && max <= Integer.MAX_VALUE )
			return new IntType();
		return new LongType();
	}
}
