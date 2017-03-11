package net.imglib2.img.basictypeaccess.volatiles.array;

import net.imglib2.img.basictypeaccess.array.AbstractCharArray;
import net.imglib2.img.basictypeaccess.volatiles.VolatileArrayDataAccess;

/**
 * @author Tobias Pietzsch
 */
public abstract class AbstractVolatileCharArray< A extends AbstractVolatileCharArray< A > >
		extends AbstractCharArray< A >
		implements VolatileArrayDataAccess< A >
{
	final protected boolean isValid;

	public AbstractVolatileCharArray( final int numEntities, final boolean isValid )
	{
		super( numEntities );
		this.isValid = isValid;
	}

	public AbstractVolatileCharArray( final char[] data, final boolean isValid )
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
