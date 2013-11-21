package net.imglib2.img.basictypeaccess.buffer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import net.imglib2.img.basictypeaccess.FloatAccess;

public class FloatBufferAccess implements FloatAccess, BufferDataAccess< FloatBufferAccess >
{
	protected FloatBuffer data;

	public FloatBufferAccess( final int numEntities )
	{
		this.data = ByteBuffer.allocateDirect( numEntities * 4 ).order(ByteOrder.nativeOrder()).asFloatBuffer();
	}

	@Override
	public float getValue( final int index )
	{
		return data.get( index );
	}

	@Override
	public void setValue( final int index, final float value )
	{
		data.put( index, value );
	}

	@Override
	public FloatBuffer getCurrentStorageArray()
	{
		return data;
	}

	@Override
	public FloatBufferAccess createArray( final int numEntities )
	{
		return new FloatBufferAccess( numEntities );
	}
}
