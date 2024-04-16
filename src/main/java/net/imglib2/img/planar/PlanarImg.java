/*
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

package net.imglib2.img.planar;

import net.imglib2.Cursor;
import net.imglib2.FlatIterationOrder;
import net.imglib2.Interval;
import net.imglib2.img.AbstractNativeImg;
import net.imglib2.img.NativeImg;
import net.imglib2.img.basictypeaccess.PlanarAccess;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.stream.LocalizableSpliterator;
import net.imglib2.type.NativeType;
import net.imglib2.util.Fraction;
import net.imglib2.util.Intervals;
import net.imglib2.util.Util;
import net.imglib2.view.iteration.SubIntervalIterable;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link NativeImg} that stores data in an list of primitive arrays, one per
 * image plane.
 * <p>
 * The {@link PlanarImg} provides access to the underlying data arrays via the
 * {@link #getPlane(int)} method.
 * </p>
 *
 * @author Jan Funke
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Johannes Schindelin
 * @author Tobias Pietzsch
 */
public class PlanarImg< T extends NativeType< T >, A extends ArrayDataAccess< A > > extends AbstractNativeImg< T, A > implements PlanarAccess< A >, SubIntervalIterable< T >
{
	final protected int numSlices;

	final int elementsPerSlice;

	/*
	 * duplicate of long[] dimension as an int array.
	 */
	final protected int[] dimensions;

	final protected int[] sliceSteps;

	final protected List< A > mirror;

	public PlanarImg( final List< A > slices, final long[] dim, final Fraction entitiesPerPixel )
	{
		super( dim, entitiesPerPixel );
		this.dimensions = Util.long2int( dim );
		this.sliceSteps = computeSliceSteps( dim );
		this.numSlices = numberOfSlices( dim );
		this.elementsPerSlice = elementsPerSlice( dim );
		if(slices.size() != numSlices)
			throw new IllegalArgumentException();
		this.mirror = slices;
	}

	/** @deprecated Use {@link #PlanarImg(List, long[], Fraction)} instead. */
	@Deprecated
	public PlanarImg( final long[] dim, final Fraction entitiesPerPixel )
	{
		this( emptySlices( dim ), dim, entitiesPerPixel );
	}

	PlanarImg( final A creator, final long[] dim, final Fraction entitiesPerPixel )
	{
		this( createSlices( creator, dim, entitiesPerPixel ), dim, entitiesPerPixel );
	}

	/**
	 * This interface is implemented by all samplers on the {@link PlanarImg}.
	 * It allows the container to ask for the slice the sampler is currently in.
	 */
	public interface PlanarContainerSampler
	{
		/**
		 * @return the index of the slice the sampler is currently accessing.
		 */
		int getCurrentSliceIndex();
	}

	@SuppressWarnings("unchecked")
	@Override
	public A update( final Object c )
	{
		final int i = ( ( PlanarContainerSampler ) c ).getCurrentSliceIndex();
		return ( A ) mirror.get( i < 0 ? 0 : ( i >= numSlices ? numSlices - 1 : i ) ).createView( c );
	}

	/**
	 * @return total number of image planes
	 */
	public int numSlices()
	{
		return numSlices;
	}

	/**
	 * For a given &ge;2d location, estimate the pixel index in the stack slice.
	 *
	 * @param l
	 * @return
	 *
	 * 		TODO: remove this method? (it doesn't seem to be used anywhere)
	 */
	public final int getIndex( final int[] l )
	{
		if ( n > 1 )
			return l[ 1 ] * dimensions[ 0 ] + l[ 0 ];
		return l[ 0 ];
	}

	/**
	 * Compute a global position from the index of a slice and an index within
	 * that slice.
	 *
	 * @param sliceIndex
	 *            index of slice
	 * @param indexInSlice
	 *            index of element within slice
	 * @param position
	 *            receives global position of element
	 *
	 *            TODO: move this method to AbstractPlanarCursor? (that seems to
	 *            be the only place where it is needed)
	 */
	public void indexToGlobalPosition( int sliceIndex, final int indexInSlice, final int[] position )
	{
		if ( n > 1 )
		{
			position[ 1 ] = indexInSlice / dimensions[ 0 ];
			position[ 0 ] = indexInSlice - position[ 1 ] * dimensions[ 0 ];

			if ( n > 2 )
			{
				final int maxDim = n - 1;

				for ( int d = 2; d < maxDim; ++d )
				{
					final int j = sliceIndex / dimensions[ d ];
					position[ d ] = sliceIndex - j * dimensions[ d ];
					sliceIndex = j;
				}
				position[ maxDim ] = sliceIndex;
			}
		}
		else
		{
			position[ 0 ] = indexInSlice;
		}
	}

	/**
	 * Compute a global position from the index of a slice and an index within
	 * that slice.
	 *
	 * @param sliceIndex
	 *            index of slice
	 * @param indexInSlice
	 *            index of element within slice
	 * @param dim
	 *            which dimension of the position we are interested in
	 * @return dimension dim of global position
	 *
	 *         TODO: move this method to AbstractPlanarCursor? (that seems to be
	 *         the only place where it is needed)
	 */
	public int indexToGlobalPosition( final int sliceIndex, final int indexInSlice, final int dim )
	{
		if ( dim == 0 )
			return indexInSlice % dimensions[ 0 ];
		else if ( dim == 1 )
			return indexInSlice / dimensions[ 0 ];
		else if ( dim < n )
			return ( sliceIndex / sliceSteps[ dim ] ) % dimensions[ dim ];
		else
			return 0;
	}

	@Override
	public PlanarCursor< T > cursor()
	{
		if ( n == 1 )
			return new PlanarCursor1D< T >( this );
		else if ( n == 2 )
			return new PlanarCursor2D< T >( this );
		else
			return new PlanarCursor< T >( this );
	}

	@Override
	public PlanarLocalizingCursor< T > localizingCursor()
	{
		if ( n == 1 )
			return new PlanarLocalizingCursor1D< T >( this );
		else if ( n == 2 )
			return new PlanarLocalizingCursor2D< T >( this );
		else
			return new PlanarLocalizingCursor< T >( this );
	}

	@Override
	public PlanarRandomAccess< T > randomAccess()
	{
		if ( n == 1 )
			return new PlanarRandomAccess1D< T >( this );
		return new PlanarRandomAccess< T >( this );
	}

	@Override
	public FlatIterationOrder iterationOrder()
	{
		return new FlatIterationOrder( this );
	}

	@Override
	public A getPlane( final int no )
	{
		return mirror.get( no );
	}

	@Override
	public void setPlane( final int no, final A plane )
	{
		mirror.set( no, plane );
	}

	@Override
	public PlanarImgFactory< T > factory()
	{
		return new PlanarImgFactory<>( linkedType );
	}

	@Override
	public PlanarImg< T, ? > copy()
	{
		final PlanarImg< T, ? > copy = factory().create( dimension );

		final PlanarCursor< T > cursor1 = this.cursor();
		final PlanarCursor< T > cursor2 = copy.cursor();

		while ( cursor1.hasNext() )
			cursor2.next().set( cursor1.next() );

		return copy;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean supportsOptimizedCursor( final Interval interval )
	{
		// we want to optimize exactly one plane
		return Intervals.contains( this, interval ) && correspondsToPlane( interval );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object subIntervalIterationOrder( final Interval interval )
	{
		return new FlatIterationOrder( interval );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Cursor< T > cursor( final Interval interval )
	{
		assert ( supportsOptimizedCursor( interval ) );

		return new PlanarPlaneSubsetCursor< T >( this, interval );
	}

	@Override
	public LocalizableSpliterator< T > spliterator()
	{
		return new PlanarSpliterator<>( this, 0, size() );
	}

	@Override
	public LocalizableSpliterator< T > localizingSpliterator()
	{
		// TODO: Implement PlanarLocalizingSpliterator
		return spliterator();
	}

	private boolean correspondsToPlane( final Interval interval )
	{
		// check if interval describes one plane
		if ( interval.dimension( 0 ) != dimension[ 0 ] )
			return false;

		if ( dimension.length == 1 )
			return true;

		if ( interval.dimension( 1 ) != dimension[ 1 ] )
			return false;

		for ( int d = 2; d < interval.numDimensions(); ++d )
		{
			if ( interval.dimension( d ) != 1 )
				return false;
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Cursor< T > localizingCursor( final Interval interval )
	{
		assert ( supportsOptimizedCursor( interval ) );

		return new PlanarPlaneSubsetLocalizingCursor< T >( this, interval );
	}

	/**
	 * What is the size of one plane in a PlanarImg with the given dimensions?
	 */
	public static int elementsPerSlice( final long[] dimensions )
	{
		int size = ( int ) dimensions[ 0 ];
		if ( dimensions.length > 1 )
			size *= ( int ) dimensions[ 1 ];
		return size;
	}

	/**
	 * How many slices has a PlanarImg with the given dimensions?
	 */
	public static int numberOfSlices( final long[] dimensions )
	{
		int s = 1;
		for ( int d = 2; d < dimensions.length; ++d )
			s *= dimensions[ d ];
		return s;
	}

	// -- Helper methods --

	private static int[] computeSliceSteps( final long[] dimensions )
	{
		final int n = dimensions.length;
		if ( n <= 2 )
			return null;
		final int[] sliceSteps = new int[ n ];
		sliceSteps[ 2 ] = 1;
		for ( int i = 3; i < n; ++i )
			sliceSteps[ i ] = ( int ) dimensions[ i - 1 ] * sliceSteps[ i - 1 ];
		return sliceSteps;
	}

	private static < A > List< A > emptySlices( final long[] dim )
	{
		final int numSlices = numberOfSlices( dim );
		final List< A > mirror = new ArrayList<>( numSlices );
		for ( int i = 0; i < numSlices; ++i )
			mirror.add( null );
		return mirror;
	}

	private static < A extends ArrayDataAccess< A > > List< A > createSlices( final A creator, final long[] dim, final Fraction entitiesPerPixel )
	{
		final int numSlices = numberOfSlices( dim );
		final List< A > mirror = new ArrayList<>( numSlices );
		final int pixelsPerPlane = (int) (( ( dim.length > 1 ) ? dim[ 1 ] : 1 ) * dim[ 0 ]);
		final int numEntitiesPerSlice = ( int ) entitiesPerPixel.mulCeil( pixelsPerPlane );
		for ( int i = 0; i < numSlices; ++i )
			mirror.add( creator.createArray( numEntitiesPerSlice ) );
		return mirror;
	}
}
