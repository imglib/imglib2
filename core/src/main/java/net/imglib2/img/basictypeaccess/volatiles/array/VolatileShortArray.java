package net.imglib2.img.basictypeaccess.volatiles.array;

import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.basictypeaccess.array.ShortArray;
import net.imglib2.img.basictypeaccess.volatiles.VolatileShortAccess;

/**
 * A {@link ShortArray} with an {@link #isValid()} flag.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class VolatileShortArray implements VolatileShortAccess, ArrayDataAccess< VolatileShortArray >
{
	private final boolean isValid;

	protected short data[];

	public VolatileShortArray( final int numEntities, final boolean isValid )
	{
		this.data = new short[ numEntities ];
		this.isValid = isValid;
	}

	public VolatileShortArray( final short[] data, final boolean isValid )
	{
		this.data = data;
		this.isValid = isValid;
	}

	@Override
	public short getValue( final int index )
	{
		return data[ index ];
	}

	@Override
	public void setValue( final int index, final short value )
	{
		data[ index ] = value;
	}

	@Override
	public VolatileShortArray createArray( final int numEntities )
	{
		return new VolatileShortArray( numEntities, true );
	}

	@Override
	public short[] getCurrentStorageArray()
	{
		return data;
	}

	@Override
	public boolean isValid()
	{
		return isValid;
	}
}
