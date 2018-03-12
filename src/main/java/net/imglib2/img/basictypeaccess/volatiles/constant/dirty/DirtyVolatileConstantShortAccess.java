package net.imglib2.img.basictypeaccess.volatiles.constant.dirty;

import net.imglib2.Dirty;
import net.imglib2.img.basictypeaccess.volatiles.constant.VolatileConstantShortAccess;

/**
 *
 * @author Philipp Hanslovsky
 *
 */
public class DirtyVolatileConstantShortAccess extends VolatileConstantShortAccess implements Dirty
{
	private boolean dirty = false;

	public DirtyVolatileConstantShortAccess( final boolean isValid )
	{
		super( isValid );
	}

	public DirtyVolatileConstantShortAccess( final short value, final boolean isValid )
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
	public void setValue( final int index, final short value )
	{
		setDirty();
		super.setValue( index, value );
	}

}
