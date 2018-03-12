package net.imglib2.img.basictypeaccess.volatiles.constant.dirty;

import net.imglib2.Dirty;
import net.imglib2.img.basictypeaccess.volatiles.constant.VolatileConstantIntAccess;

/**
 *
 * @author Philipp Hanslovsky
 *
 */
public class DirtyVolatileConstantIntAccess extends VolatileConstantIntAccess implements Dirty
{
	private boolean dirty = false;

	public DirtyVolatileConstantIntAccess( final boolean isValid )
	{
		super( isValid );
	}

	public DirtyVolatileConstantIntAccess( final int value, final boolean isValid )
	{
		super( value, isValid );
	}

	@Override
	public boolean isDirty()
	{
		return this.dirty;
	}

	@Override
	public void setDirty()
	{
		this.dirty = true;
	}

	@Override
	public void setValue( final int index, final int value )
	{
		setDirty();
		super.setValue( index, value );
	}

}
