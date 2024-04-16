/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2024 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
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
