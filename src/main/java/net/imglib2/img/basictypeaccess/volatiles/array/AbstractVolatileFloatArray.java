package net.imglib2.img.basictypeaccess.volatiles.array;

import net.imglib2.img.basictypeaccess.array.AbstractFloatArray;
import net.imglib2.img.basictypeaccess.volatiles.VolatileArrayDataAccess;

/**
 * @author Tobias Pietzsch
 */
public abstract class AbstractVolatileFloatArray< A extends AbstractVolatileFloatArray< A > >
		extends AbstractFloatArray< A >
		implements VolatileArrayDataAccess< A >
{
	final protected boolean isValid;

	public AbstractVolatileFloatArray( final int numEntities, final boolean isValid )
	{
		super( numEntities );
		this.isValid = isValid;
	}

	public AbstractVolatileFloatArray( final float[] data, final boolean isValid )
	{
		super( data );
		this.isValid = isValid;
	}

	@Override
	public boolean isValid()
	{
		return isValid;
	}

	@Override
	public A createArray( final int numEntities )
	{
		return createArray( numEntities, true );
	}
}
