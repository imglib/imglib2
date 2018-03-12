package net.imglib2.img.basictypeaccess.constant.dirty;

import net.imglib2.Dirty;
import net.imglib2.img.basictypeaccess.constant.ConstantCharAccess;

/**
 *
 * @author Philipp Hanslovsky
 *
 */
public class DirtyConstantCharAccess extends ConstantCharAccess implements Dirty
{
	private boolean dirty = false;

	public DirtyConstantCharAccess()
	{
		super();
	}

	public DirtyConstantCharAccess( final char value )
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
	public void setValue( final int index, final char value )
	{
		setDirty();
		super.setValue( index, value );
	}

}
