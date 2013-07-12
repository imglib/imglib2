package net.imglib2.img.basictypeaccess.unsafe;

import java.lang.reflect.Field;

import net.imglib2.img.basictypeaccess.ByteAccess;
import sun.misc.Unsafe;

public class ByteUnsafeAccess implements ByteAccess, UnsafeDataAccess< ByteUnsafeAccess >
{
	private static final Unsafe unsafe;
	static
	{
		try
		{
			final Field field = Unsafe.class.getDeclaredField( "theUnsafe" );
			field.setAccessible( true );
			unsafe = ( Unsafe ) field.get( null );
		}
		catch ( final Exception e )
		{
			throw new RuntimeException( e );
		}
	}

	protected long address;

	public ByteUnsafeAccess( final int numEntities )
	{
		address = unsafe.allocateMemory( numEntities );
	}

	@Override
	public byte getValue( final int index )
	{
		return unsafe.getByte( address + index );
	}

	@Override
	public void setValue( final int index, final byte value )
	{
		unsafe.putByte( address + index, value );
	}

	@Override
	public Long getCurrentStorageArray()
	{
		return address;
	}

	@Override
	public ByteUnsafeAccess createArray( final int numEntities )
	{
		return new ByteUnsafeAccess( numEntities );
	}

	@Override
	protected void finalize() throws Throwable
	{
		unsafe.freeMemory( address );
		super.finalize();
	}
}
