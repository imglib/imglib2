package net.imglib2.img.basictypeaccess.volatiles.array;

import net.imglib2.img.basictypeaccess.array.AbstractIntArray;
import net.imglib2.img.basictypeaccess.volatiles.VolatileArrayDataAccess;

/**
 * @author Tobias Pietzsch
 */
public abstract class AbstractVolatileIntArray< A extends AbstractVolatileIntArray< A > >
		extends AbstractIntArray< A >
		implements VolatileArrayDataAccess< A >
{
	final protected boolean isValid;

	public AbstractVolatileIntArray( final int numEntities, final boolean isValid )
	{
		super( numEntities );
		this.isValid = isValid;
	}

	public AbstractVolatileIntArray( final int[] data, final boolean isValid )
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
