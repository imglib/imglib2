/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
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

package net.imglib2.labeling;

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

import net.imglib2.type.numeric.IntegerType;

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
 *            TODO
 * 
 * @author Lee Kamentsky
 */
public class LabelingMapping< T extends Comparable< T >>
{
	final List< T > theEmptyList;

	private final int maxNumLabels;

	public LabelingMapping( final IntegerType< ? > value )
	{
		maxNumLabels = ( int ) value.getMaxValue();

		final List< T > background = Collections.emptyList();
		theEmptyList = intern( background );
	}

	private static class InternedList< T1 extends Comparable< T1 >> implements List< T1 >
	{
		private final List< T1 > value;

		final int index;

		final LabelingMapping< T1 > owner;

		// final LabelingMapping<L1> owner;

		public InternedList( final List< T1 > src, final int index, final LabelingMapping< T1 > owner )
		{
			this.owner = owner;
			this.value = Collections.unmodifiableList( src );
			this.index = index;
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

	protected Map< List< T >, InternedList< T >> internedLists = new HashMap< List< T >, InternedList< T >>();

	protected List< InternedList< T >> listsByIndex = new ArrayList< InternedList< T >>();

	public List< T > emptyList()
	{
		return theEmptyList;
	}

	/**
	 * Return the canonical list for the given list
	 */
	public synchronized List< T > intern( final List< T > src )
	{
		return internImpl( src );
	}

	private InternedList< T > internImpl( List< T > src )
	{

		InternedList< T > interned;

		if ( src instanceof InternedList )
		{
			interned = ( InternedList< T > ) src;
			if ( interned.owner == this ) { return interned; }
		}
		if ( src.size() != 0 )
		{
			final List< T > copy = new ArrayList< T >( src );
			Collections.sort( copy );
			src = copy;
		}

		interned = internedLists.get( src );

		if ( interned == null )
		{
			synchronized ( listsByIndex )
			{

				final int intIndex = listsByIndex.size();

				if ( intIndex > maxNumLabels )
					throw new AssertionError( String.format( "Too many labels (or types of multiply-labeled pixels): %d maximum", intIndex ) );

				interned = new InternedList< T >( src, intIndex, this );
				listsByIndex.add( interned );
				internedLists.put( src, interned );
			}
		}

		return interned;
	}

	public List< T > intern( final T[] src )
	{
		return intern( Arrays.asList( src ) );
	}

	public int indexOf( final List< T > key )
	{
		final InternedList< T > interned = internImpl( key );
		return interned.index;
	}

	public int indexOf( final T[] key )
	{
		return indexOf( intern( key ) );
	}

	public final List< T > listAtIndex( final int index )
	{
		return listsByIndex.get( index );
	}

	/**
	 * Returns the number of indexed labeling lists
	 */
	public int numLists()
	{
		return listsByIndex.size();
	}

	/**
	 * TODO
	 * 
	 * @return the labels defined in the mapping.
	 */
	public List< T > getLabels()
	{
		final HashSet< T > result = new HashSet< T >();
		for ( final InternedList< T > instance : listsByIndex )
		{
			for ( final T label : instance )
			{
				result.add( label );
			}
		}
		return new ArrayList< T >( result );
	}
}
