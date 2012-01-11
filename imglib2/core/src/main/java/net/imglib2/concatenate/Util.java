package net.imglib2.concatenate;

import java.util.List;
import java.util.ListIterator;

/**
 * Utilities for manipulating lists of (pre-)concatenable objects.
 *
 * @author Tobias Pietzsch
 */
public class Util
{
	public static void print( final List< ? > concatenatables )
	{
		final ListIterator< ? > iterator = concatenatables.listIterator();
		System.out.print( " ====  " );

		while ( iterator.hasNext() )
		{
			final Object a = iterator.next();
			System.out.print( a );
			if ( iterator.hasNext() )
				System.out.print( " x " );
		}
		System.out.println();
	}

	/**
	 * Reduce a list of objects by iteratively pre/concatenating neighboring
	 * objects if they support it.
	 *
	 * @param objects
	 *            list of objects that will be reduced in place
	 */
	@SuppressWarnings( { "unchecked", "rawtypes" } )
	public static < T > void join( final List< T > objects )
	{
		// print( objects );
		int oldConcatenablesSize;
		do
		{
			oldConcatenablesSize = objects.size();
			if ( oldConcatenablesSize >= 2 )
			{
				final ListIterator< T > iterator = objects.listIterator();
				T c1 = null;
				T c2 = iterator.next();
				while ( iterator.hasNext() )
				{
					c1 = c2;
					c2 = iterator.next();
					if ( Concatenable.class.isInstance( c1 ) && ( ( Concatenable ) c1 ).getConcatenableClass().isInstance( c2 ) )
					{
						c2 = ( T ) ( ( Concatenable ) c1 ).concatenate( c2 );
						iterator.remove();
						iterator.previous();
						iterator.set( c2 );
						iterator.next();
					}
					else if ( PreConcatenable.class.isInstance( c2 ) && ( ( PreConcatenable ) c2 ).getPreConcatenableClass().isInstance( c1 ) )
					{
						c2 = ( T ) ( ( PreConcatenable ) c2 ).preConcatenate( c1 );
						iterator.previous();
						iterator.previous();
						iterator.remove();
						iterator.next();
						iterator.set( c2 );
					}
				}
			}
			// print( objects );
		}
		while ( oldConcatenablesSize != objects.size() );
	}
}
