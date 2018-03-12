package net.imglib2.img.basictypeaccess.volatiles.constant.dirty;

import net.imglib2.Dirty;
import net.imglib2.img.basictypeaccess.volatiles.constant.VolatileConstantByteAccess;

/**
 *
 * @author Philipp Hanslovsky
 *
 */
public class DirtyVolatileConstantByteAccess extends VolatileConstantByteAccess implements Dirty
{
	private boolean dirty = false;

	public DirtyVolatileConstantByteAccess( final boolean isValid )
	{
		super( isValid );
	}

	public DirtyVolatileConstantByteAccess( final byte value, final boolean isValid )
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
	public void setValue( final int index, final byte value )
	{
		setDirty();
		super.setValue( index, value );
	}

}
