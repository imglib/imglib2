/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * @author Lee Kamentsky
 *
 */
package net.imglib2.labeling;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * The LabelingMapping maps a set of labelings of a pixel to an index value
 * which can be more compactly stored than the set of labelings. The service it
 * provides is an "intern" function that supplies a canonical object for each
 * set of labelings in a container.
 *
 * For example, say pixels are labeled with strings and a particular pixel is
 * labeled as belonging to both "Foo" and "Bar" and this is the first label
 * assigned to the container. The caller will ask for the index of { "Foo",
 * "Bar" } and get back the number, "1". LabelingMapping will work faster if the
 * caller first interns { "Foo", "Bar" } and then requests the mapping of the
 * returned object.
 *
 * @param <T>
 * @param <N>
 */
public class LabelingMapping< T extends Comparable< T >, N extends Number >
{
	Constructor< N > constructor;

	N instance;

	final List< T > theEmptyList;

	@SuppressWarnings( "unchecked" )
	public LabelingMapping( final N srcInstance )
	{
		instance = srcInstance;
		final Class< ? extends Number > c = srcInstance.getClass();
		try
		{
			constructor = ( Constructor< N > ) c.getConstructor( new Class[] { String.class } );
		}
		catch ( final SecurityException e )
		{
			e.printStackTrace();
			throw new AssertionError( e.getMessage() );
		}
		catch ( final NoSuchMethodException e )
		{
			e.printStackTrace();
			throw new AssertionError( "Number class cannot be constructed from a string" );
		}
		final List< T > background = Collections.emptyList();
		theEmptyList = intern( background );
	}

	private static class InternedList< T1 extends Comparable< T1 >, N extends Number > implements List< T1 >
	{
		private final List< T1 > value;

		final N index;

		final LabelingMapping< T1, N > owner;

		public InternedList( final List< T1 > src, final N index, final LabelingMapping< T1, N > owner )
		{
			value = Collections.unmodifiableList( src );
			this.index = index;
			this.owner = owner;
		}

		@Override
		public int size()
		{
			return value.size();
		}

		@Override
		public boolean isEmpty()
		{
			return value.isEmpty();
		}

		@Override
		public boolean contains( final Object o )
		{
			return value.contains( o );
		}

		@Override
		public Iterator< T1 > iterator()
		{
			return value.iterator();
		}

		@Override
		public Object[] toArray()
		{
			return value.toArray();
		}

		@Override
		public boolean add( final T1 e )
		{
			return value.add( e );
		}

		@Override
		public boolean remove( final Object o )
		{
			return value.remove( o );
		}

		@Override
		public boolean containsAll( final Collection< ? > c )
		{
			return value.containsAll( c );
		}

		@Override
		public boolean addAll( final Collection< ? extends T1 > c )
		{
			return value.addAll( c );
		}

		@Override
		public boolean addAll( final int i, final Collection< ? extends T1 > c )
		{
			return value.addAll( i, c );
		}

		@Override
		public boolean removeAll( final Collection< ? > c )
		{
			return value.removeAll( c );
		}

		@Override
		public boolean retainAll( final Collection< ? > c )
		{
			return value.retainAll( c );
		}

		@Override
		public void clear()
		{
			value.clear();
		}

		@Override
		public T1 get( final int i )
		{
			return value.get( i );
		}

		@Override
		public T1 set( final int index, final T1 element )
		{
			return value.set( index, element );
		}

		@Override
		public void add( final int i, final T1 element )
		{
			value.add( i, element );
		}

		@Override
		public T1 remove( final int i )
		{
			return value.remove( i );
		}

		@Override
		public int indexOf( final Object o )
		{
			return value.indexOf( o );
		}

		@Override
		public int lastIndexOf( final Object o )
		{
			return value.lastIndexOf( o );
		}

		@Override
		public ListIterator< T1 > listIterator()
		{
			return value.listIterator();
		}

		@Override
		public ListIterator< T1 > listIterator( final int i )
		{
			return value.listIterator( i );
		}

		@Override
		public List< T1 > subList( final int fromIndex, final int toIndex )
		{
			return value.subList( fromIndex, toIndex );
		}

		@Override
		public < T > T[] toArray( final T[] a )
		{
			return value.toArray( a );
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode()
		{
			return value.hashCode();
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals( final Object obj )
		{
			if ( obj instanceof InternedList )
			{
				@SuppressWarnings( "rawtypes" )
				final InternedList iobj = ( InternedList ) obj;
				return value.equals( iobj.value );
			}
			return value.equals( obj );
		}
	}

	protected Map< List< T >, InternedList< T, N >> internedLists = new HashMap< List< T >, InternedList< T, N >>();

	protected ArrayList< InternedList< T, N >> listsByIndex = new ArrayList< InternedList< T, N >>();

	public List< T > emptyList()
	{
		return theEmptyList;
	}

	/**
	 * Return the canonical list for the given list
	 *
	 * @param src
	 * @return
	 */
	public List< T > intern( final List< T > src )
	{
		return internImpl( src );
	}

	@SuppressWarnings( "unchecked" )
	private InternedList< T, N > internImpl( final List< T > src )
	{
		InternedList< T, N > interned;
		if ( src instanceof InternedList )
		{
			interned = ( InternedList< T, N > ) src;
			if ( interned.owner == this )
				return interned;
		}
		final List< T > copy = new ArrayList< T >( src );
		Collections.sort( copy );
		interned = internedLists.get( copy );
		if ( interned == null )
		{
			final int intIndex = listsByIndex.size();
			N index;
			try
			{
				index = constructor.newInstance( Integer.toString( intIndex ) );
			}
			catch ( final IllegalArgumentException e )
			{
				e.printStackTrace();
				throw new AssertionError( e.getMessage() );
			}
			catch ( final InstantiationException e )
			{
				e.printStackTrace();
				throw new AssertionError( e.getMessage() );
			}
			catch ( final IllegalAccessException e )
			{
				e.printStackTrace();
				throw new AssertionError( e.getMessage() );
			}
			catch ( final InvocationTargetException e )
			{
				e.printStackTrace();
				if ( e.getTargetException() instanceof NumberFormatException ) { throw new AssertionError( String.format( "Too many labels (or types of multiply-labeled pixels): %d maximum", intIndex ) ); }
				throw new AssertionError( e.getMessage() );
			}
			interned = new InternedList< T, N >( copy, index, this );
			listsByIndex.add( interned );
			internedLists.put( interned, interned );
		}
		return interned;
	}

	public List< T > intern( final T[] src )
	{
		return intern( Arrays.asList( src ) );
	}

	public N indexOf( final List< T > key )
	{
		final InternedList< T, N > interned = internImpl( key );
		return interned.index;
	}

	public N indexOf( final T[] key )
	{
		return indexOf( intern( key ) );
	}

	public List< T > listAtIndex( final int index )
	{
		return listsByIndex.get( index );
	}

	/**
	 * @return the labels defined in the mapping.
	 */
	public List< T > getLabels()
	{
		final HashSet< T > result = new HashSet< T >();
		for ( final InternedList< T, N > inst : listsByIndex )
		{
			for ( final T label : inst )
			{
				result.add( label );
			}
		}
		return new ArrayList< T >( result );
	}
}
