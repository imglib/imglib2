package net.imglib2.img.basictypeaccess.volatiles.array;

import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.basictypeaccess.volatiles.VolatileByteAccess;

/**
 * A {@link ByteArray} with an {@link #isValid()} flag.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class VolatileByteArray implements VolatileByteAccess, ArrayDataAccess< VolatileByteArray >
{
	private final boolean isValid;

	protected byte data[];

	public VolatileByteArray( final int numEntities, final boolean isValid )
	{
		this.data = new byte[ numEntities ];
		this.isValid = isValid;
	}

	public VolatileByteArray( final byte[] data, final boolean isValid )
	{
		this.data = data;
		this.isValid = isValid;
	}

	@Override
	public byte getValue( final int index )
	{
		return data[ index ];
	}

	@Override
	public void setValue( final int index, final byte value )
	{
		data[ index ] = value;
	}

	@Override
	public VolatileByteArray createArray( final int numEntities )
	{
		return new VolatileByteArray( numEntities, true );
	}

	@Override
	public byte[] getCurrentStorageArray()
	{
		return data;
	}

	@Override
	public boolean isValid()
	{
		return isValid;
	}
}
