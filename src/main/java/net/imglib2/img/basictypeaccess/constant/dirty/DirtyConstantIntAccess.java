package net.imglib2.img.basictypeaccess.constant.dirty;

import net.imglib2.Dirty;
import net.imglib2.img.basictypeaccess.constant.ConstantIntAccess;

/**
 *
 * @author Philipp Hanslovsky
 *
 */
public class DirtyConstantIntAccess extends ConstantIntAccess implements Dirty
{
	private boolean dirty = false;

	public DirtyConstantIntAccess()
	{
		super();
	}

	public DirtyConstantIntAccess( final int value )
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
	public void setValue( final int index, final int value )
	{
		setDirty();
		super.setValue( index, value );
	}

}
