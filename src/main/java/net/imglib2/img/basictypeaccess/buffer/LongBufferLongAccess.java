package net.imglib2.img.basictypeaccess.buffer;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;

import net.imglib2.img.basictypeaccess.LongAccess;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.basictypeaccess.volatiles.VolatileAccess;

public class LongBufferLongAccess implements ArrayDataAccess< LongBufferLongAccess >, LongAccess, VolatileAccess
{

	// TODO How to properly design a buffer access?
	// 1. Implementing ArrayDataAccess probably makes sense
	// 2. Should we have separate accesses that implement VolatileAccess?
	// 3. Should we use ByteBuffer only or use specific buffers like LongBuffer.
	//    allocateDirect is only available in ByteBuffer.
	//    Both accesses could be implemented, i.e. LongBufferLongAccess as well
	//    as ByteBufferLongAccess

	private static boolean DEFAULT_IS_VALID = true;

	private final LongBuffer buffer;

	private final boolean isValid;

	public LongBufferLongAccess( final int numEntities )
	{
		this( numEntities, DEFAULT_IS_VALID );
	}

	public LongBufferLongAccess( final int numEntities, final boolean isValid )
	{
		this( LongBuffer.allocate( numEntities ), isValid );
	}

	public LongBufferLongAccess( final ByteBuffer buffer )
	{
		this( buffer, DEFAULT_IS_VALID );
	}

	public LongBufferLongAccess( final ByteBuffer buffer, final boolean isValid )
	{
		this( buffer.asLongBuffer(), isValid );
	}

	public LongBufferLongAccess( final LongBuffer buffer )
	{
		this( buffer, DEFAULT_IS_VALID );
	}

	public LongBufferLongAccess( final LongBuffer buffer, final boolean isValid )
	{
		this.buffer = buffer;
		this.isValid = isValid;
	}

	@Override
	public boolean isValid()
	{
		return isValid;
	}

	@Override
	public long getValue( int index )
	{
		return buffer.get( index );
	}

	@Override
	public void setValue( int index, long value )
	{
		buffer.put( index, value );

	}

	@Override
	public LongBufferLongAccess createArray( int numEntities )
	{
		// TODO
		// Should we respect the kind of buffer that this.buffer is?
		// i.e. if this.buffer is direct, then the buffer of the new
		// access should also be direct? Is there a method method in
		// Buffer that allows to do that?
		return new LongBufferLongAccess( numEntities, isValid );
	}

	@Override
	public LongBuffer getCurrentStorageArray()
	{
		return buffer;
	}

	@Override
	public int getArrayLength()
	{
		return buffer.capacity();
	}

}
