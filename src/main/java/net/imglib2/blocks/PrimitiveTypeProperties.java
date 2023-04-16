package net.imglib2.blocks;

import java.util.EnumMap;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.basictypeaccess.array.BooleanArray;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.basictypeaccess.array.CharArray;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.img.basictypeaccess.array.LongArray;
import net.imglib2.img.basictypeaccess.array.ShortArray;
import net.imglib2.type.PrimitiveType;

import static net.imglib2.type.PrimitiveType.BOOLEAN;
import static net.imglib2.type.PrimitiveType.BYTE;
import static net.imglib2.type.PrimitiveType.CHAR;
import static net.imglib2.type.PrimitiveType.DOUBLE;
import static net.imglib2.type.PrimitiveType.FLOAT;
import static net.imglib2.type.PrimitiveType.INT;
import static net.imglib2.type.PrimitiveType.LONG;
import static net.imglib2.type.PrimitiveType.SHORT;

/**
 * @param <P> a primitive array type, e.g., {@code byte[]}.
 * @param <A> the corresponding {@code ArrayDataAccess} type.
 */
class PrimitiveTypeProperties< P, A extends ArrayDataAccess< A > >
{
	final Class< P > primitiveArrayClass;

	final IntFunction< P > createPrimitiveArray;

	final ToIntFunction< P > primitiveArrayLength;

	final Function< P, A > wrapAsAccess;

	static PrimitiveTypeProperties< ?, ? > get( final PrimitiveType primitiveType )
	{
		final PrimitiveTypeProperties< ?, ? > props = creators.get( primitiveType );
		if ( props == null )
			throw new IllegalArgumentException();
		return props;
	}

	/**
	 * Wrap a primitive array {@code data} into a corresponding {@code ArrayDataAccess}.
	 *
	 * @param data primitive array to wrap (actually type {@code P} instead of {@code Object}, but its easier to use this way)
	 * @return {@code ArrayDataAccess} wrapping {@code data}
	 */
	A wrap( Object data )
	{
		if ( data == null )
			throw new NullPointerException();
		if ( !primitiveArrayClass.isInstance( data ) )
			throw new IllegalArgumentException( "expected " + primitiveArrayClass.getSimpleName() + " argument" );
		return wrapAsAccess.apply( ( P ) data );
	}

	/**
	 * Allocate a primitive array (type {@code P}) with {@code length} elements.
	 */
	P allocate( int length )
	{
		return createPrimitiveArray.apply( length );
	}

	/**
	 * Get the length of a primitive array (type {@code P}).
	 */
	int length( P array )
	{
		return primitiveArrayLength.applyAsInt( array );
	}

	private PrimitiveTypeProperties(
			final Class< P > primitiveArrayClass,
			final IntFunction< P > createPrimitiveArray,
			final ToIntFunction< P > primitiveArrayLength,
			final Function< P, A > wrapAsAccess )
	{
		this.primitiveArrayClass = primitiveArrayClass;
		this.createPrimitiveArray = createPrimitiveArray;
		this.primitiveArrayLength = primitiveArrayLength;
		this.wrapAsAccess = wrapAsAccess;
	}

	private static final EnumMap< PrimitiveType, PrimitiveTypeProperties< ?, ? > > creators = new EnumMap<>( PrimitiveType.class );

	static
	{
		creators.put( BOOLEAN, new PrimitiveTypeProperties<>( boolean[].class, boolean[]::new, a -> a.length, BooleanArray::new ) );
		creators.put( BYTE, new PrimitiveTypeProperties<>( byte[].class, byte[]::new, a -> a.length, ByteArray::new ) );
		creators.put( CHAR, new PrimitiveTypeProperties<>( char[].class, char[]::new, a -> a.length, CharArray::new ) );
		creators.put( SHORT, new PrimitiveTypeProperties<>( short[].class, short[]::new, a -> a.length, ShortArray::new ) );
		creators.put( INT, new PrimitiveTypeProperties<>( int[].class, int[]::new, a -> a.length, IntArray::new ) );
		creators.put( LONG, new PrimitiveTypeProperties<>( long[].class, long[]::new, a -> a.length, LongArray::new ) );
		creators.put( FLOAT, new PrimitiveTypeProperties<>( float[].class, float[]::new, a -> a.length, FloatArray::new ) );
		creators.put( DOUBLE, new PrimitiveTypeProperties<>( double[].class, double[]::new, a -> a.length, DoubleArray::new ) );
	}
}
