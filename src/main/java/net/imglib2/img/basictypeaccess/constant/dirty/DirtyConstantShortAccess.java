package net.imglib2.img.basictypeaccess.constant.dirty;

import net.imglib2.Dirty;
import net.imglib2.img.basictypeaccess.constant.ConstantShortAccess;

/**
 *
 * @author Philipp Hanslovsky
 *
 */
public class DirtyConstantShortAccess extends ConstantShortAccess implements Dirty
{
	private boolean dirty = false;

	public DirtyConstantShortAccess()
	{
		super();
	}

	public DirtyConstantShortAccess( final short value )
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
	public void setValue( final int index, final short value )
	{
		setDirty();
		super.setValue( index, value );
	}

}
