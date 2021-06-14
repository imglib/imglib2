package net.imglib2.type.numeric.integer;

import java.util.Arrays;
import java.util.List;

import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.IntegerType;

public class IntegerTypes
{
	private static List< ? extends IntegerType< ? > > signedTypes = Arrays.asList( new ByteType(), new ShortType(), new IntType(), new LongType() );

	private static List< ? extends IntegerType< ? > > unsignedTypes = Arrays.asList( new BitType(), new UnsignedByteType(), new UnsignedShortType(), new UnsignedIntType(), new UnsignedLongType() );

	private IntegerTypes()
	{
		// NB: prevent instantiation of static utility class
	}

	/**
	 * Get an {@link IntegerType} that can hold the given {@code max} value.
	 * 
	 * The smallest byte-size type matching the constraints will be selected.
	 * 
	 * @param signed
	 *            - {@code true} if a signed {@link IntegerType} is required
	 * @param max
	 *            - the expected maximum value
	 * @return an {@link IntegerType} matching the given constraints
	 */
	public static IntegerType< ? > smallestType( boolean signed, long max )
	{
		return smallestType( signed ? -1 : 0, max );
	}

	/**
	 * Get an {@link IntegerType} that can hold the given {@code min} and
	 * {@code max} values.
	 * 
	 * The smallest byte-size type matching the constraints will be selected,
	 * with a preference for unsigned types.
	 * 
	 * @param min
	 *            - the expected minimum value
	 * @param max
	 *            - the expected maximum value
	 * @return an {@link IntegerType} matching the given constraints
	 */
	public static IntegerType< ? > smallestType( long min, long max )
	{
		if ( min > max )
			throw new IllegalArgumentException( "Wrong usage: min (" + min + ") > max (" + max + ")" );
		if ( min >= 0 ) // unsigned
		{
			return unsignedTypes.stream().filter( i -> min >= i.getMinValue() && max <= i.getMaxValue() ).findFirst().get().createVariable();
		}
		return signedTypes.stream().filter( i -> min >= i.getMinValue() && max <= i.getMaxValue() ).findFirst().get().createVariable();
	}
}
