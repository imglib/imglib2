package net.imglib2.img.basictypeaccess.constant.dirty;

import net.imglib2.Dirty;
import net.imglib2.img.basictypeaccess.constant.ConstantDoubleAccess;

/**
 *
 * @author Philipp Hanslovsky
 *
 */
public class DirtyConstantDoubleAccess extends ConstantDoubleAccess implements Dirty
{
	private boolean dirty = false;

	public DirtyConstantDoubleAccess()
	{
		super();
	}

	public DirtyConstantDoubleAccess( final double value )
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
	public void setValue( final int index, final double value )
	{
		setDirty();
		super.setValue( index, value );
	}

}
