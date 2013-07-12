package net.imglib2.img.basictypeaccess.unsafe;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

public class UnsafeUtils
{
	public static Unsafe getUnsafe()
	{
		try
		{
			final Field f = Unsafe.class.getDeclaredField( "theUnsafe" );
			f.setAccessible( true );
			return ( Unsafe ) f.get( null );
		}
		catch ( final Exception e )
		{}
		return null;
	}

	public static void main( final String[] args )
	{
		System.out.println( getUnsafe() );
		final Unsafe unsafe = getUnsafe();
	}
}
