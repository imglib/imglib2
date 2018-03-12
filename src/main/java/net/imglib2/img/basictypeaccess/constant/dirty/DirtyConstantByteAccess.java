package net.imglib2.img.basictypeaccess.constant.dirty;

import net.imglib2.Dirty;
import net.imglib2.img.basictypeaccess.constant.ConstantByteAccess;

/**
 *
 * @author Philipp Hanslovsky
 *
 */
public class DirtyConstantByteAccess extends ConstantByteAccess implements Dirty
{
	private boolean dirty = false;

	public DirtyConstantByteAccess()
	{
		super();
	}

	public DirtyConstantByteAccess( final byte value )
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
	public void setValue( final int index, final byte value )
	{
		setDirty();
		super.setValue( index, value );
	}

}
