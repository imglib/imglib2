package net.imglib2.img.basictypeaccess.unsafe;

import net.imglib2.img.basictypeaccess.FloatAccess;
import sun.misc.Unsafe;

public class FloatUnsafeAccess implements FloatAccess, UnsafeDataAccess< FloatUnsafeAccess >
{
	private static final Unsafe unsafe = UnsafeUtils.getUnsafe();

	protected final long address;

	public FloatUnsafeAccess( final int numEntities )
	{
		address = unsafe.allocateMemory( ( ( long ) numEntities ) << 2 );
	}

	@Override
	public float getValue( final int index )
	{
		return unsafe.getFloat( address + ( ( ( long ) index << 2 ) ) );
	}

	@Override
	public void setValue( final int index, final float value )
	{
		unsafe.putFloat( address + ( ( ( long ) index ) << 2 ), value );
	}

	@Override
	public Long getCurrentStorageArray()
	{
		return address;
	}

	@Override
	public FloatUnsafeAccess createArray( final int numEntities )
	{
		return new FloatUnsafeAccess( numEntities );
	}

	@Override
	protected void finalize() throws Throwable
	{
		unsafe.freeMemory( address );
		super.finalize();
	}
}
