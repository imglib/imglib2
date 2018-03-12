package net.imglib2.img.basictypeaccess.volatiles.constant.dirty;

import net.imglib2.Dirty;
import net.imglib2.img.basictypeaccess.volatiles.constant.VolatileConstantCharAccess;

/**
 *
 * @author Philipp Hanslovsky
 *
 */
public class DirtyVolatileConstantCharAccess extends VolatileConstantCharAccess implements Dirty
{
	private boolean dirty = false;

	public DirtyVolatileConstantCharAccess( final boolean isValid )
	{
		super( isValid );
	}

	public DirtyVolatileConstantCharAccess( final char value, final boolean isValid )
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
	public void setValue( final int index, final char value )
	{
		setDirty();
		super.setValue( index, value );
	}

}
