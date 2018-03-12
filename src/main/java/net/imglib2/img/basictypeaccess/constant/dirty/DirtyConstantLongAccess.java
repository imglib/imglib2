package net.imglib2.img.basictypeaccess.constant.dirty;

import net.imglib2.Dirty;
import net.imglib2.img.basictypeaccess.constant.ConstantLongAccess;

/**
 *
 * @author Philipp Hanslovsky
 *
 */
public class DirtyConstantLongAccess extends ConstantLongAccess implements Dirty
{
	private boolean dirty = false;

	public DirtyConstantLongAccess()
	{
		super();
	}

	public DirtyConstantLongAccess( final long value )
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
	public void setValue( final int index, final long value )
	{
		setDirty();
		super.setValue( index, value );
	}

}
