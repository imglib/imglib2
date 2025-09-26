package net.imglib2.blocks;

public final class VolatileArray< T >
{
	private final T data;

	private final byte[] valid;

	public VolatileArray( final T data, final byte[] valid )
	{
		this.data = data;
		this.valid = valid;
	}

	VolatileArray( final PrimitiveTypeProperties< T, ? > props, final int length )
	{
		this.data = props.allocate( length );
		this.valid = new byte[ length ];
	}

	public T data()
	{
		return data;
	}

	public byte[] valid()
	{
		return valid;
	}

	public int length()
	{
		return valid.length;
	}
}
