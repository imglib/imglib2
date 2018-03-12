package net.imglib2.img.basictypeaccess.volatiles.constant.dirty;

import net.imglib2.Dirty;
import net.imglib2.img.basictypeaccess.volatiles.constant.VolatileConstantFloatAccess;

/**
 *
 * @author Philipp Hanslovsky
 *
 */
public class DirtyVolatileConstantFloatAccess extends VolatileConstantFloatAccess implements Dirty
{
	private boolean dirty = false;

	public DirtyVolatileConstantFloatAccess( final boolean isValid )
	{
		super( isValid );
	}

	public DirtyVolatileConstantFloatAccess( final float value, final boolean isValid )
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
	public void setValue( final int index, final float value )
	{
		setDirty();
		super.setValue( index, value );
	}

}
