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

package net.imglib2.algorithm.componenttree.pixellist;

import java.util.Iterator;

import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.util.IntervalIndexer;

/**
 * A singly-linked list of pixel locations stored in a {@link RandomAccessible}.
 * (the value at a given location in the {@link RandomAccessible} is the index
 * of the next location in the list.)
 * 
 * 
 * @author Tobias Pietzsch
 */
public final class PixelList implements Iterable< Localizable >
{
	/**
	 * RandomAccess into the index image to store the linked list.
	 */
	private final RandomAccess< LongType > locationsAccess;

	/**
	 * Dimensions of the index image.
	 */
	private final long[] dimensions;

	/**
	 * Index of first location in the list.
	 */
	private long headIndex;

	/**
	 * Last location in the list.
	 */
	private final long[] tailPos;

	/**
	 * length of the list.
	 */
	private long size;

	/**
	 * @param locationsAccess
	 *            RandomAccess into the index image to store the linked list.
	 * @param dimensions
	 *            Dimensions of the index image.
	 */
	public PixelList( final RandomAccess< LongType > locationsAccess, final long[] dimensions )
	{
		this.locationsAccess = locationsAccess;
		this.dimensions = dimensions;
		headIndex = 0;
		tailPos = new long[ dimensions.length ];
		size = 0;
	}

	public PixelList( final PixelList l )
	{
		this.locationsAccess = l.locationsAccess;
		this.dimensions = l.dimensions;
		this.headIndex = l.headIndex;
		this.tailPos = null;
		this.size = l.size;
	}

	/**
	 * Append a pixel location to the list.
	 */
	public void addPosition( final Localizable position )
	{
		if ( size == 0 )
		{
			position.localize( tailPos );
			final long i = IntervalIndexer.positionToIndex( tailPos, dimensions );
			headIndex = i;
		}
		else
		{
			locationsAccess.setPosition( tailPos );
			position.localize( tailPos );
			final long i = IntervalIndexer.positionToIndex( tailPos, dimensions );
			locationsAccess.get().set( i );
		}
		++size;
	}

	/**
	 * Append another {@link PixelList} to this one.
	 */
	public void merge( final PixelList l )
	{
		if ( size == 0 )
		{
			headIndex = l.headIndex;
			for ( int i = 0; i < tailPos.length; ++i )
				tailPos[ i ] = l.tailPos[ i ];
		}
		else
		{
			locationsAccess.setPosition( tailPos );
			locationsAccess.get().set( l.headIndex );
			for ( int i = 0; i < tailPos.length; ++i )
				tailPos[ i ] = l.tailPos[ i ];
		}
		size += l.size;
	}

	public class PixelListIterator implements Iterator< Localizable >
	{
		private long i;

		private long nextIndex;

		private final long[] tmp;

		private final Point pos;

		public PixelListIterator()
		{
			i = 0;
			nextIndex = headIndex;
			tmp = new long[ dimensions.length ];
			pos = new Point( dimensions.length );
		}

		@Override
		public boolean hasNext()
		{
			return i < size;
		}

		@Override
		public Localizable next()
		{
			++i;
			IntervalIndexer.indexToPosition( nextIndex, dimensions, tmp );
			pos.setPosition( tmp );
			locationsAccess.setPosition( tmp );
			nextIndex = locationsAccess.get().get();
			return pos;
		}

		@Override
		public void remove()
		{}
	}

	@Override
	public Iterator< Localizable > iterator()
	{
		return new PixelListIterator();
	}

	/**
	 * Get the size of the list.
	 * 
	 * @return number of elements in this list.
	 */
	public long size()
	{
		return size;
	}

	/**
	 * empty the list.
	 */
	public void clear()
	{
		size = 0;
	}
}
