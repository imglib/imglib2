package net.imglib2.img.basictypeaccess.unsafe;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

public class UnsafeUtils
{
	private static Unsafe unsafe= null;

	public static Unsafe getUnsafe()
	{
		if ( unsafe == null)
		{
			try
			{
				final Field f = Unsafe.class.getDeclaredField( "theUnsafe" );
				f.setAccessible( true );
				return ( Unsafe ) f.get( null );
			}
			catch ( final Exception e )
			{}
		}
		return unsafe;
	}
}
