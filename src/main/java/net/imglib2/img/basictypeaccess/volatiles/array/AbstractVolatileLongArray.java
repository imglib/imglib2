package net.imglib2.img.basictypeaccess.volatiles.array;

import net.imglib2.img.basictypeaccess.array.AbstractLongArray;
import net.imglib2.img.basictypeaccess.volatiles.VolatileArrayDataAccess;

/**
 * @author Tobias Pietzsch
 */
public abstract class AbstractVolatileLongArray< A extends AbstractVolatileLongArray< A > >
		extends AbstractLongArray< A >
		implements VolatileArrayDataAccess< A >
{
	final protected boolean isValid;

	public AbstractVolatileLongArray( final int numEntities, final boolean isValid )
	{
		super( numEntities );
		this.isValid = isValid;
	}

	public AbstractVolatileLongArray( final long[] data, final boolean isValid )
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
