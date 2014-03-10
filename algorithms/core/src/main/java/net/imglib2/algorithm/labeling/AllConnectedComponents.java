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

package net.imglib2.algorithm.labeling;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.labeling.Labeling;
import net.imglib2.labeling.LabelingOutOfBoundsRandomAccessFactory;
import net.imglib2.labeling.LabelingType;
import net.imglib2.outofbounds.OutOfBounds;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.type.logic.BitType;
import net.imglib2.view.Views;

/**
 * Label all 8-connected components of a binary image
 * 
 * @author Lee Kamentsky
 */
public class AllConnectedComponents
{
	protected static class PositionStack
	{
		private final int dimensions;

		private long[] storage;

		private int position = 0;

		public PositionStack( final int dimensions )
		{
			this.dimensions = dimensions;
			storage = new long[ 100 * dimensions ];
		}

		public void push( final long[] position )
		{
			final int insertPoint = this.position * dimensions;
			if ( storage.length == insertPoint )
			{
				final long[] newStorage = new long[ ( this.position * 3 / 2 ) * dimensions ];
				System.arraycopy( storage, 0, newStorage, 0, storage.length );
				storage = newStorage;
			}
			System.arraycopy( position, 0, storage, insertPoint, dimensions );
			this.position++;
		}

		public void pop( final long[] position )
		{
			this.position--;
			System.arraycopy( storage, this.position * dimensions, position, 0, dimensions );
		}

		public boolean isEmpty()
		{
			return position == 0;
		}
	}

	/**
	 * Label all connected components in the given image using an 8-connected
	 * structuring element or it's N-dimensional analog (connect if touching
	 * along diagonals as well as +/- one element in any direction).
	 * 
	 * @param <T>
	 *            the type of the labels to apply
	 * @param labeling
	 *            Assign labels to this labeling space
	 * @param img
	 *            a binary image where true indicates parts of components
	 * @param names
	 *            supplies names for the different components as needed
	 * @throws NoSuchElementException
	 *             if there are not enough names
	 */
	public static < T extends Comparable< T >> void labelAllConnectedComponents( final Labeling< T > labeling, final RandomAccessibleInterval< BitType > img, final Iterator< T > names ) throws NoSuchElementException
	{
		final long[][] offsets = getStructuringElement( img.numDimensions() );
		labelAllConnectedComponents( labeling, img, names, offsets );
	}

	/**
	 * Label all connected components in the given image using an arbitrary
	 * structuring element.
	 * 
	 * @param <T>
	 *            the type of the labels to apply
	 * @param labeling
	 *            Assign labels to this labeling space
	 * @param img
	 *            a binary image where true indicates parts of components
	 * @param names
	 *            supplies names for the different components as needed
	 * @param structuringElement
	 *            an array of offsets to a pixel of the pixels which are
	 *            considered connected. For instance, a 4-connected structuring
	 *            element would be "new int [][] {{-1,0},{1,0},{0,-1},{0,1}}".
	 * @throws NoSuchElementException
	 *             if there are not enough names
	 */
	public static < T extends Comparable< T >> void labelAllConnectedComponents( final Labeling< T > labeling, final RandomAccessibleInterval< BitType > img, final Iterator< T > names, final long[][] structuringElement ) throws NoSuchElementException
	{
		final Cursor< BitType > c = Views.iterable( img ).localizingCursor();
		final RandomAccess< BitType > raSrc = img.randomAccess();
		final OutOfBoundsFactory< LabelingType< T >, Labeling< T >> factory = new LabelingOutOfBoundsRandomAccessFactory< T, Labeling< T >>();
		final OutOfBounds< LabelingType< T >> raDest = factory.create( labeling );
		final long[] srcPosition = new long[ img.numDimensions() ];
		final long[] destPosition = new long[ labeling.numDimensions() ];
		final long[] dimensions = new long[ labeling.numDimensions() ];
		labeling.dimensions( dimensions );
		final PositionStack toDoList = new PositionStack( img.numDimensions() );
		while ( c.hasNext() )
		{
			final BitType t = c.next();
			if ( t.get() )
			{
				c.localize( srcPosition );
				boolean outOfBounds = false;
				for ( int i = 0; i < dimensions.length; i++ )
				{
					if ( srcPosition[ i ] >= dimensions[ i ] )
					{
						outOfBounds = true;
						break;
					}
				}
				if ( outOfBounds )
					continue;

				raDest.setPosition( srcPosition );
				/*
				 * Assign a label if no label has yet been assigned.
				 */
				LabelingType< T > label = raDest.get();
				if ( label.getLabeling().isEmpty() )
				{
					final List< T > currentLabel = label.intern( names.next() );
					label.setLabeling( currentLabel );
					toDoList.push( srcPosition );
					while ( !toDoList.isEmpty() )
					{
						/*
						 * Find neighbors at the position
						 */
						toDoList.pop( srcPosition );
						for ( final long[] offset : structuringElement )
						{
							outOfBounds = false;
							for ( int i = 0; i < offset.length; i++ )
							{
								destPosition[ i ] = srcPosition[ i ] + offset[ i ];
								if ( ( destPosition[ i ] < 0 ) || ( destPosition[ i ] >= dimensions[ i ] ) )
								{
									outOfBounds = true;
									break;
								}
							}
							if ( outOfBounds )
								continue;
							raSrc.setPosition( destPosition );
							if ( raSrc.get().get() )
							{
								raDest.setPosition( destPosition );
								label = raDest.get();
								if ( label.getLabeling().isEmpty() )
								{
									label.setLabeling( currentLabel );
									toDoList.push( destPosition );
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Return an array of offsets to the 8-connected (or N-d equivalent)
	 * structuring element for the dimension space. The structuring element is
	 * the list of offsets from the center to the pixels to be examined.
	 * 
	 * @param dimensions
	 * @return the structuring element.
	 */
	static public long[][] getStructuringElement( final int dimensions )
	{
		int nElements = 1;
		for ( int i = 0; i < dimensions; i++ )
			nElements *= 3;
		nElements--;
		final long[][] result = new long[ nElements ][ dimensions ];
		final long[] position = new long[ dimensions ];
		Arrays.fill( position, -1 );
		for ( int i = 0; i < nElements; i++ )
		{
			System.arraycopy( position, 0, result[ i ], 0, dimensions );
			/*
			 * Special case - skip the center element.
			 */
			if ( i == nElements / 2 - 1 )
			{
				position[ 0 ] += 2;
			}
			else
			{
				for ( int j = 0; j < dimensions; j++ )
				{
					if ( position[ j ] == 1 )
					{
						position[ j ] = -1;
					}
					else
					{
						position[ j ]++;
						break;
					}
				}
			}
		}
		return result;
	}

	/**
	 * Return an iterator that (endlessly) dispenses increasing integer values
	 * for labeling components.
	 * 
	 * @param start
	 * @return an iterator dispensing Integers
	 */
	static public Iterator< Integer > getIntegerNames( final int start )
	{
		return new Iterator< Integer >()
		{
			int current = start;

			@Override
			public boolean hasNext()
			{
				return true;
			}

			@Override
			public Integer next()
			{
				return current++;
			}

			@Override
			public void remove()
			{

			}
		};
	}
}
