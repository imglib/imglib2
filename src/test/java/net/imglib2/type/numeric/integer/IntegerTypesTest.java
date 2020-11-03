package net.imglib2.type.numeric.integer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

import net.imglib2.type.logic.BitType;

public class IntegerTypesTest
{

	@Test
	public void testIntegerTypes()
	{
		/* smallestType(min, max) */
		assertEquals( BitType.class, IntegerTypes.smallestType( 0, 1 ).getClass() );
		assertEquals( UnsignedByteType.class, IntegerTypes.smallestType( 0, 128 ).getClass() );
		assertEquals( UnsignedByteType.class, IntegerTypes.smallestType( 0, 255 ).getClass() );
		assertEquals( UnsignedShortType.class, IntegerTypes.smallestType( 0, 256 ).getClass() );
		assertEquals( UnsignedShortType.class, IntegerTypes.smallestType( 0, 65535 ).getClass() );
		assertEquals( UnsignedIntType.class, IntegerTypes.smallestType( 0, 65536 ).getClass() );
		assertEquals( UnsignedIntType.class, IntegerTypes.smallestType( 0, ( long ) Integer.MAX_VALUE - Integer.MIN_VALUE ).getClass() );
		assertEquals( UnsignedLongType.class, IntegerTypes.smallestType( 0, ( long ) Integer.MAX_VALUE - Integer.MIN_VALUE + 1 ).getClass() );

		assertEquals( ByteType.class, IntegerTypes.smallestType( -128, 0 ).getClass() );
		assertEquals( ShortType.class, IntegerTypes.smallestType( -129, 0 ).getClass() );
		assertEquals( ShortType.class, IntegerTypes.smallestType( -32768, 0 ).getClass() );
		assertEquals( IntType.class, IntegerTypes.smallestType( -32769, 0 ).getClass() );
		assertEquals( IntType.class, IntegerTypes.smallestType( Integer.MIN_VALUE, 0 ).getClass() );
		assertEquals( LongType.class, IntegerTypes.smallestType( ( long ) Integer.MIN_VALUE - 1, 0 ).getClass() );

		/* smallestType(signed, max) */
		assertEquals( UnsignedByteType.class, IntegerTypes.smallestType( false, 128 ).getClass() );
		assertEquals( ShortType.class, IntegerTypes.smallestType( true, 128 ).getClass() );

		assertEquals( UnsignedShortType.class, IntegerTypes.smallestType( false, 32768 ).getClass() );
		assertEquals( IntType.class, IntegerTypes.smallestType( true, 32768 ).getClass() );

		assertEquals( UnsignedIntType.class, IntegerTypes.smallestType( false, ( long ) Integer.MAX_VALUE + 1 ).getClass() );
		assertEquals( LongType.class, IntegerTypes.smallestType( true, ( long ) Integer.MAX_VALUE + 1 ).getClass() );

		/* wrong usage */
		assertThrows( IllegalArgumentException.class, () -> IntegerTypes.smallestType( 0, -1 ) );
	}

}
