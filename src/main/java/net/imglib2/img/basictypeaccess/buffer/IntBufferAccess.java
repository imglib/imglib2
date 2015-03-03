package net.imglib2.img.basictypeaccess.buffer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import net.imglib2.img.basictypeaccess.IntAccess;

public class IntBufferAccess implements IntAccess, BufferDataAccess< IntBufferAccess >
{
	protected IntBuffer data;

	public IntBufferAccess( final int numEntities )
	{
		this.data = ByteBuffer.allocateDirect( numEntities * 4 ).order(ByteOrder.nativeOrder()).asIntBuffer();
	}

	@Override
	public int getValue( final int index )
	{
		return data.get( index );
	}

	@Override
	public void setValue( final int index, final int value )
	{
		data.put( index, value );
	}

	@Override
	public IntBuffer getCurrentStorageArray()
	{
		return data;
	}

	@Override
	public IntBufferAccess createArray( final int numEntities )
	{
		return new IntBufferAccess( numEntities );
	}
}
