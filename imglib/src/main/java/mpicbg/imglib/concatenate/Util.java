package mpicbg.imglib.concatenate;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class Util
{
	public static void print( List< Concatenatable < ? > > concatenatables )
	{
		ListIterator< Concatenatable< ? > > iterator = concatenatables.listIterator();
		System.out.print(" ====  ");
		
		while( iterator.hasNext() )
		{
			Concatenatable< ? > c = iterator.next();
			System.out.print( c );
			if ( iterator.hasNext() )
				System.out.print( " x " );
		}
		System.out.println();
	}

	@SuppressWarnings( { "unchecked", "rawtypes" } )
	public static void join( List< Concatenatable < ? > > concatenatables )
	{
		print( concatenatables );
		int oldConcatenablesSize;
		do
		{
			oldConcatenablesSize = concatenatables.size();
			if ( oldConcatenablesSize >= 2 )
			{
				ListIterator< Concatenatable< ? > > iterator = concatenatables.listIterator();
				Concatenatable< ? > c1 = null;
				Concatenatable< ? > c2 = iterator.next();
				while( iterator.hasNext() )
				{
					c1 = c2;
					c2 = iterator.next();				
					if ( c1.getConcatenatableClass().isInstance( c2 ) )
					{
						( ( Concatenatable ) c1 ).concatenate( c2 );
						c2 = c1;
						iterator.remove();					
					}
					else if ( c2.getConcatenatableClass().isInstance( c1 ) )
					{
						( ( Concatenatable ) c2 ).preConcatenate( c1 );
						iterator.previous();
						iterator.previous();
						iterator.remove();
						iterator.next();
					}
				}
			}
			print( concatenatables );
		}
		while ( oldConcatenablesSize != concatenatables.size() );
	}

	public static void main( String[] args )
	{
		List< Concatenatable< ? > > transforms = new ArrayList< Concatenatable< ? > >();
		transforms.add( new Rotation2D("R1") );
		transforms.add( new Rotation2D("R2") );
		transforms.add( new Translation2D("T1") );
		transforms.add( new Rigid2D("A1") );
		transforms.add( new Translation2D("T2") );
		
		join( transforms );
	}
}
