package mpicbg.imglib.concatenate;

import java.util.List;
import java.util.ListIterator;

public class Util
{
	public static void print( List< ? > concatenatables )
	{
		ListIterator< ? > iterator = concatenatables.listIterator();
		System.out.print(" ====  ");
		
		while( iterator.hasNext() )
		{
			Object a = iterator.next();
			System.out.print( a );
			if ( iterator.hasNext() )
				System.out.print( " x " );
		}
		System.out.println();
	}

	@SuppressWarnings( { "unchecked", "rawtypes" } )
	public static < T > void join( List< T > objects )
	{
		print( objects );
		int oldConcatenablesSize;
		do
		{
			oldConcatenablesSize = objects.size();
			if ( oldConcatenablesSize >= 2 )
			{
				ListIterator< T > iterator = objects.listIterator();
				T c1 = null;
				T c2 = iterator.next();
				while( iterator.hasNext() )
				{
					c1 = c2;
					c2 = iterator.next();
					if ( Concatenable.class.isInstance( c1 ) && ( ( Concatenable ) c1).getConcatenableClass().isInstance( c2 ) )
					{
						c2 = ( T ) ( ( Concatenable ) c1 ).concatenate( c2 );
						iterator.remove();
						iterator.previous();
						iterator.set( c2 );
						iterator.next();
					}
					else if ( PreConcatenable.class.isInstance( c2 ) && ( ( PreConcatenable ) c2).getPreConcatenableClass().isInstance( c1 ) )
					{
						c2 =  ( T ) ( ( PreConcatenable ) c2 ).preConcatenate( c1 );
						iterator.previous();
						iterator.previous();
						iterator.remove();
						iterator.next();
						iterator.set( c2 );
					}
				}
			}
			print( objects );
		}
		while ( oldConcatenablesSize != objects.size() );
	}
}
