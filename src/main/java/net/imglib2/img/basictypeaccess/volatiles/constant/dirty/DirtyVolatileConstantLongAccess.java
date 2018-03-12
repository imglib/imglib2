package net.imglib2.img.basictypeaccess.volatiles.constant.dirty;

import net.imglib2.Dirty;
import net.imglib2.img.basictypeaccess.volatiles.constant.VolatileConstantLongAccess;

/**
 *
 * @author Philipp Hanslovsky
 *
 */
public class DirtyVolatileConstantLongAccess extends VolatileConstantLongAccess implements Dirty
{
	private boolean dirty = false;

	public DirtyVolatileConstantLongAccess( final boolean isValid )
	{
		super( isValid );
	}

	public DirtyVolatileConstantLongAccess( final long value, final boolean isValid )
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
	public void setValue( final int index, final long value )
	{
		setDirty();
		super.setValue( index, value );
	}

}
