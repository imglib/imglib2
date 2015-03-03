package net.imglib2.img.basictypeaccess.buffer;

import java.nio.ByteBuffer;

import net.imglib2.img.basictypeaccess.ByteAccess;

public class ByteBufferAccess implements ByteAccess, BufferDataAccess< ByteBufferAccess >
{
	protected ByteBuffer data;

	public ByteBufferAccess( final int numEntities )
	{
		this.data = ByteBuffer.allocateDirect( numEntities );
	}

	@Override
	public byte getValue( final int index )
	{
		return data.get( index );
	}

	@Override
	public void setValue( final int index, final byte value )
	{
		data.put( index, value );
	}

	@Override
	public ByteBuffer getCurrentStorageArray()
	{
		return data;
	}

	@Override
	public ByteBufferAccess createArray( final int numEntities )
	{
		return new ByteBufferAccess( numEntities );
	}
}
