package net.imglib2.img.basictypeaccess.volatiles.constant.dirty;

import net.imglib2.Dirty;
import net.imglib2.img.basictypeaccess.volatiles.constant.VolatileConstantDoubleAccess;

/**
 *
 * @author Philipp Hanslovsky
 *
 */
public class DirtyVolatileConstantDoubleAccess extends VolatileConstantDoubleAccess implements Dirty
{
	private boolean dirty = false;

	public DirtyVolatileConstantDoubleAccess( final boolean isValid )
	{
		super( isValid );
	}

	public DirtyVolatileConstantDoubleAccess( final double value, final boolean isValid )
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
	public void setValue( final int index, final double value )
	{
		setDirty();
		super.setValue( index, value );
	}

}
