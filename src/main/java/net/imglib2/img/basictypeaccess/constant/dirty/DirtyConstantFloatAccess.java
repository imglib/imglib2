package net.imglib2.img.basictypeaccess.constant.dirty;

import net.imglib2.Dirty;
import net.imglib2.img.basictypeaccess.constant.ConstantFloatAccess;

/**
 *
 * @author Philipp Hanslovsky
 *
 */
public class DirtyConstantFloatAccess extends ConstantFloatAccess implements Dirty
{
	private boolean dirty = false;

	public DirtyConstantFloatAccess()
	{
		super();
	}

	public DirtyConstantFloatAccess( final float value )
	{
		super( value );
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
