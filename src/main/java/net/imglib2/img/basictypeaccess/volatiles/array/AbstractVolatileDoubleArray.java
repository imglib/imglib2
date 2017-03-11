package net.imglib2.img.basictypeaccess.volatiles.array;

import net.imglib2.img.basictypeaccess.array.AbstractDoubleArray;
import net.imglib2.img.basictypeaccess.volatiles.VolatileArrayDataAccess;

/**
 * @author Tobias Pietzsch
 */
public abstract class AbstractVolatileDoubleArray< A extends AbstractVolatileDoubleArray< A > >
		extends AbstractDoubleArray< A >
		implements VolatileArrayDataAccess< A >
{
	final protected boolean isValid;

	public AbstractVolatileDoubleArray( final int numEntities, final boolean isValid )
	{
		super( numEntities );
		this.isValid = isValid;
	}

	public AbstractVolatileDoubleArray( final double[] data, final boolean isValid )
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
