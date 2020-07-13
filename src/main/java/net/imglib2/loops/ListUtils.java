package net.imglib2.loops;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class ListUtils
{
	static Object[] concatAsArray( Object action, List< ? > samplers )
	{
		Object[] result = new Object[ samplers.size() + 1 ];
		result[0] = action;
		for ( int i = 0; i < samplers.size(); i++ )
			result[ i + 1 ] = samplers.get( i );
		return result;
	}

	public static <T, R> List<R> map( final Function< T, R > function, final List< T > list ) {
		List<R> result = new ArrayList<>( list.size() );
		for ( T entry : list )
			result.add( function.apply( entry ) );
		return result;
	}

	public static <T> boolean allMatch( Predicate<T> predicate, List<T> list )
	{
		for ( T entry : list )
			if( !predicate.test( entry ) )
				return false;
		return true;
	}

	public static <T, R> List<R> map( final Function< T, R > function, final T[] list ) {
		List<R> result = new ArrayList<>( list.length );
		for ( T entry : list )
			result.add( function.apply( entry ) );
		return result;
	}

	public static <T> boolean allMatch( Predicate<T> predicate, T[] list )
	{
		for ( T entry : list )
			if( !predicate.test( entry ) )
				return false;
		return true;
	}

	public static <T> boolean anyMatch( Predicate<T> predicate, T[] list )
	{
		for ( T entry : list )
			if( predicate.test( entry ) )
				return true;
		return false;
	}
}
