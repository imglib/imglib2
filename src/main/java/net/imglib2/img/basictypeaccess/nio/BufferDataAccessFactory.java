package net.imglib2.img.basictypeaccess.nio;

import static net.imglib2.img.basictypeaccess.AccessFlags.DIRTY;

import java.nio.ByteBuffer;
import java.util.Set;

import net.imglib2.img.basictypeaccess.AccessFlags;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.basictypeaccess.volatiles.array.DirtyVolatileByteArray;
import net.imglib2.type.NativeType;
import net.imglib2.type.NativeTypeFactory;
import net.imglib2.type.PrimitiveType;


/**
 * Given a {@link PrimitiveType} and {@link AccessFlags} creates a specific
 * {@link ArrayDataAccess}. For example, {@code BYTE} with flags {@code DIRTY}
 * and {@code VOLATILE} specifies {@link DirtyVolatileByteArray}.
 *
 * @author Tobias Pietzsch
 */
public class BufferDataAccessFactory
{
	public static < T extends NativeType< T >, A extends BufferAccess< A > > A get(
			final T type )
	{
		return get( type, AccessFlags.setOf() );
	}

	public static < T extends NativeType< T >, A extends BufferAccess< A > > A get(
			final T type,
			final Set< AccessFlags > flags )
	{
		return get( type.getNativeTypeFactory().getPrimitiveType(), flags );
	}

	public static < A extends BufferAccess< A > > A get(
			final NativeTypeFactory< ?, ? > typeFactory )
	{
		return get( typeFactory.getPrimitiveType(), AccessFlags.setOf() );
	}

	public static < A extends BufferAccess< A > > A get(
			final NativeTypeFactory< ?, ? > typeFactory,
			final Set< AccessFlags > flags )
	{
		return get( typeFactory.getPrimitiveType(), flags );
	}

	/**
	 * Get a {@code BufferAccess} instance with the given {@code AccessFlags}
	 * for the given {@code PrimitiveType}.
	 * <p>
	 * The returned {@code BufferAccess} can be used as a factory for accesses
	 * of the same type using the {@link BufferAccess#newInstance} or {@link
	 * ArrayDataAccess#createArray} methods.
	 *
	 * @param primitiveType
	 * 		Java primitive types
	 * @param flags
	 * 		set of access flags ({@code DIRTY}, {@code VOLATILE}).
	 *
	 * @return a {@code BufferAccess} instance.
	 */
	@SuppressWarnings( "unchecked" )
	public static < A extends BufferAccess< A > > A get(
			final PrimitiveType primitiveType,
			final Set< AccessFlags > flags )
	{
		final boolean dirty = flags.contains( DIRTY );
		if ( dirty )
			// TODO: implement DirtyByteBufferAccess etc.
			throw new UnsupportedOperationException( "TODO: implement DirtyByteBufferAccess etc." );
		final ByteBuffer buf = ByteBuffer.allocateDirect( 8 );
		switch ( primitiveType )
		{
		case BOOLEAN:
			throw new UnsupportedOperationException( "TODO: so far, no Boolean BufferAccess exists." );
		case BYTE:
			return ( A ) ByteBufferAccess.fromByteBuffer( buf, true );
		case CHAR:
			return ( A ) CharBufferAccess.fromByteBuffer( buf, true );
		case DOUBLE:
			return ( A ) DoubleBufferAccess.fromByteBuffer( buf, true );
		case FLOAT:
			return ( A ) FloatBufferAccess.fromByteBuffer( buf, true );
		case INT:
			return ( A ) IntBufferAccess.fromByteBuffer( buf, true );
		case LONG:
			return ( A ) LongBufferAccess.fromByteBuffer( buf, true );
		case SHORT:
			return ( A ) ShortBufferAccess.fromByteBuffer( buf, true );
		default:
			throw new IllegalArgumentException();
		}
	}
}
