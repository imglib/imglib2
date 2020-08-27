/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2020 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
package net.imglib2.interpolation.stack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.EuclideanSpace;
import net.imglib2.RealRandomAccessible;

/**
 * A {@link List} of {@link RealRandomAccessible}s that is also a
 * {@link EuclideanSpace}.
 *
 * @param <T>
 */
public class RealRandomAccessibleStack< T > extends AbstractEuclideanSpace implements List< RealRandomAccessible< T > >
{
	protected final List< RealRandomAccessible< T > > slices;

	public RealRandomAccessibleStack( final List< RealRandomAccessible< T > > slices )
	{
		super( slices.get( 0 ).numDimensions() + 1 );

		this.slices = slices;
	}

	public RealRandomAccessibleStack( final int n )
	{
		super( n );

		this.slices = new ArrayList<>();
	}

	@Override
	public int size()
	{
		return slices.size();
	}

	@Override
	public boolean isEmpty()
	{
		return slices.isEmpty();
	}

	@Override
	public boolean contains( final Object o )
	{
		return slices.contains( o );
	}

	@Override
	public Iterator< RealRandomAccessible< T > > iterator()
	{
		return slices.iterator();
	}

	@Override
	public Object[] toArray()
	{
		return slices.toArray();
	}

	@Override
	public < S > S[] toArray( final S[] a )
	{
		return slices.toArray( a );
	}

	@Override
	public boolean add( final RealRandomAccessible< T > e )
	{
		return slices.add( e );
	}

	@Override
	public boolean remove( final Object o )
	{
		return slices.remove( o );
	}

	@Override
	public boolean containsAll( final Collection< ? > c )
	{
		return slices.containsAll( c );
	}

	@Override
	public boolean addAll( final Collection< ? extends RealRandomAccessible< T > > c )
	{
		return slices.addAll( c );
	}

	@Override
	public boolean addAll( final int index, final Collection< ? extends RealRandomAccessible< T > > c )
	{
		return slices.addAll( index, c );
	}

	@Override
	public boolean removeAll( final Collection< ? > c )
	{
		return slices.removeAll( c );
	}

	@Override
	public boolean retainAll( final Collection< ? > c )
	{
		return slices.retainAll( c );
	}

	@Override
	public void clear()
	{
		slices.clear();
	}

	@Override
	public RealRandomAccessible< T > get( final int index )
	{
		return slices.get( index );
	}

	@Override
	public RealRandomAccessible< T > set( final int index, final RealRandomAccessible< T > element )
	{
		return slices.set( index, element );
	}

	@Override
	public void add( final int index, final RealRandomAccessible< T > element )
	{
		slices.add( index, element );
	}

	@Override
	public RealRandomAccessible< T > remove( final int index )
	{
		return slices.remove( index );
	}

	@Override
	public int indexOf( final Object o )
	{
		return slices.indexOf( o );
	}

	@Override
	public int lastIndexOf( final Object o )
	{
		return slices.lastIndexOf( o );
	}

	@Override
	public ListIterator< RealRandomAccessible< T > > listIterator()
	{
		return slices.listIterator();
	}

	@Override
	public ListIterator< RealRandomAccessible< T > > listIterator( final int index )
	{
		return slices.listIterator( index );
	}

	@Override
	public List< RealRandomAccessible< T > > subList( final int fromIndex, final int toIndex )
	{
		return new RealRandomAccessibleStack<>( slices.subList( fromIndex, toIndex ) );
	}
}
