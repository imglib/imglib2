package net.imglib2.img.basictypeaccess.volatiles.array;

import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.img.basictypeaccess.volatiles.VolatileFloatAccess;

/**
 * A {@link FloatArray} with an {@link #isValid()} flag.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class VolatileFloatArray implements VolatileFloatAccess, ArrayDataAccess< VolatileFloatArray >
{
	private final boolean isValid;

	protected float data[];

	public VolatileFloatArray( final int numEntities, final boolean isValid )
	{
		this.data = new float[ numEntities ];
		this.isValid = isValid;
	}

	public VolatileFloatArray( final float[] data, final boolean isValid )
	{
		this.data = data;
		this.isValid = isValid;
	}

	@Override
	public float getValue( final int index )
	{
		return data[ index ];
	}

	@Override
	public void setValue( final int index, final float value )
	{
		data[ index ] = value;
	}

	@Override
	public VolatileFloatArray createArray( final int numEntities )
	{
		return new VolatileFloatArray( numEntities, true );
	}

	@Override
	public float[] getCurrentStorageArray()
	{
		return data;
	}

	@Override
	public boolean isValid()
	{
		return isValid;
	}
}
