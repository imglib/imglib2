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

package net.imglib2.algorithm.componenttree;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.PriorityQueue;

import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.componenttree.pixellist.PixelListComponentTree;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.Type;
import net.imglib2.type.logic.BitType;

/**
 * Build the component tree of an image. This is an implementation of the
 * algorithm described by D. Nister and H. Stewenius in
 * "Linear Time Maximally Stable Extremal Regions" (ECCV 2008).
 * 
 * The input to the algorithm is a RandomAccessibleInterval< T >. Further, a
 * Comparator<T> and a {@link PartialComponent.Generator} to instantiate new
 * components are required. Pixel locations are aggregated in
 * {@link PartialComponent}s which are passed to a
 * {@link PartialComponent.Handler} whenever a connected component for a
 * specific threshold is completed.
 * 
 * Building up a tree structure out of the completed components should happen in
 * the {@link PartialComponent.Handler} implementation. See
 * {@link PixelListComponentTree} for an example.
 * 
 * <p>
 * <strong>TODO</strong> Add support for non-zero-min RandomAccessibleIntervals.
 * (Currently, we assume that the input image is a <em>zero-min</em> interval.)
 * </p>
 * 
 * @param <T>
 *            value type of the input image.
 * @param <C>
 *            component type.
 * 
 * @author Tobias Pietzsch
 */
public final class BuildComponentTree< T extends Type< T >, C extends PartialComponent< T, C > >
{
	/**
	 * Run the algorithm. Completed components are emitted to the
	 * {@link PartialComponent.Handler} which is responsible for building up the
	 * tree structure. An implementations of {@link PartialComponent.Handler} is
	 * provided for example by {@link PixelListComponentTree}.
	 * 
	 * @param input
	 *            input image.
	 * @param componentGenerator
	 *            provides new {@link PartialComponent} instances.
	 * @param componentHandler
	 *            receives completed {@link PartialComponent}s.
	 * @param comparator
	 *            determines ordering of threshold values.
	 */
	public static < T extends Type< T >, C extends PartialComponent< T, C > > void buildComponentTree(
			final RandomAccessibleInterval< T > input,
			final PartialComponent.Generator< T, C > componentGenerator,
			final PartialComponent.Handler< C > componentHandler,
			final Comparator< T > comparator )
	{
		new BuildComponentTree< T, C >( input, componentGenerator, componentHandler, comparator );
	}

	/**
	 * Run the algorithm. Completed components are emitted to the
	 * {@link PartialComponent.Handler} which is responsible for building up the
	 * tree structure. An implementations of {@link PartialComponent.Handler} is
	 * provided for example by {@link PixelListComponentTree}.
	 * 
	 * @param input
	 *            input image of a comparable value type.
	 * @param componentGenerator
	 *            provides new {@link PartialComponent} instances.
	 * @param componentHandler
	 *            receives completed {@link PartialComponent}s.
	 * @param darkToBright
	 *            determines ordering of threshold values. If it is true, then
	 *            thresholds are applied from low to high values. Note that the
	 *            {@link PartialComponent.Generator#createMaxComponent()} needs
	 *            to match this ordering. For example when IntType using
	 *            darkToBright=false, then
	 *            {@link PartialComponent.Generator#createMaxComponent()} should
	 *            provide a Integer.MIN_VALUE valued component.
	 */
	public static < T extends Type< T > & Comparable< T >, C extends PartialComponent< T, C > > void buildComponentTree(
			final RandomAccessibleInterval< T > input,
			final PartialComponent.Generator< T, C > componentGenerator,
			final PartialComponent.Handler< C > componentHandler,
			final boolean darkToBright )
	{
		new BuildComponentTree< T, C >( input, componentGenerator, componentHandler, darkToBright ? new DarkToBright< T >() : new BrightToDark< T >() );
	}

	/**
	 * Default comparator for {@link Comparable} pixel values for dark-to-bright
	 * pass.
	 */
	public static final class DarkToBright< T extends Comparable< T > > implements Comparator< T >
	{
		@Override
		public int compare( final T o1, final T o2 )
		{
			return o1.compareTo( o2 );
		}
	}

	/**
	 * Default comparator for {@link Comparable} pixel values for bright-to-dark
	 * pass.
	 */
	public static final class BrightToDark< T extends Comparable< T > > implements Comparator< T >
	{
		@Override
		public int compare( final T o1, final T o2 )
		{
			return o2.compareTo( o1 );
		}
	}

	/**
	 * Iterate pixel positions in 4-neighborhood.
	 */
	private static final class Neighborhood
	{
		/**
		 * index of the next neighbor to visit. 0 is pixel at x-1, 1 is pixel at
		 * x+1, 2 is pixel at y-1, 3 is pixel at y+1, and so on.
		 */
		private int n;

		/**
		 * number of neighbors, e.g., 4 for 2d images.
		 */
		private final int nBound;

		/**
		 * image dimensions. used to check out-of-bounds.
		 */
		final long[] dimensions;

		public Neighborhood( final long[] dim )
		{
			n = 0;
			nBound = dim.length * 2;
			dimensions = dim;
		}

		public int getNextNeighborIndex()
		{
			return n;
		}

		public void setNextNeighborIndex( final int n )
		{
			this.n = n;
		}

		public void reset()
		{
			n = 0;
		}

		public boolean hasNext()
		{
			return n < nBound;
		}

		/**
		 * Set neighbor to the next (according to
		 * {@link BuildComponentTree.Neighborhood#n}) neighbor position of
		 * current. Assumes that prior to any call to next() neighbor was a the
		 * same position as current, i.e. neighbor position is only modified
		 * incrementally.
		 * 
		 * @param current
		 * @param neighbor
		 * @return false if the neighbor position is out of bounds, true
		 *         otherwise.
		 */
		public boolean next( final Localizable current, final Positionable neighbor, final Positionable neighbor2 )
		{
			final int d = n / 2;
			final boolean bck = ( n == 2 * d ); // n % 2 == 0
			++n;
			if ( bck )
			{
				if ( d > 0 )
				{
					neighbor.setPosition( current.getLongPosition( d - 1 ), d - 1 );
					neighbor2.setPosition( current.getLongPosition( d - 1 ), d - 1 );
				}
				final long dpos = current.getLongPosition( d ) - 1;
				neighbor.setPosition( dpos, d );
				neighbor2.setPosition( dpos, d );
				return dpos >= 0;
			}
			else
			{
				final long dpos = current.getLongPosition( d ) + 1;
				neighbor.setPosition( dpos, d );
				neighbor2.setPosition( dpos, d );
				return dpos < dimensions[ d ];
			}
		}
	}

	/**
	 * A pixel position on the heap of boundary pixels to be processed next. The
	 * heap is sorted by pixel values.
	 */
	private final class BoundaryPixel extends Point implements Comparable< BoundaryPixel >
	{
		private final T value;

		// TODO: this should be some kind of iterator over the neighborhood
		private int nextNeighborIndex;

		public BoundaryPixel( final Localizable position, final T value, final int nextNeighborIndex )
		{
			super( position );
			this.nextNeighborIndex = nextNeighborIndex;
			this.value = value.copy();
		}

		public int getNextNeighborIndex()
		{
			return nextNeighborIndex;
		}

		public T get()
		{
			return value;
		}

		@Override
		public int compareTo( final BoundaryPixel o )
		{
			return comparator.compare( value, o.value );
		}
	}

	private final ArrayDeque< BoundaryPixel > reusableBoundaryPixels;

	private BoundaryPixel createBoundaryPixel( final Localizable position, final T value, final int nextNeighborIndex )
	{
		if ( reusableBoundaryPixels.isEmpty() )
			return new BoundaryPixel( position, value, nextNeighborIndex );
		else
		{
			final BoundaryPixel p = reusableBoundaryPixels.pop();
			p.setPosition( position );
			p.value.set( value );
			p.nextNeighborIndex = nextNeighborIndex;
			return p;
		}
	}

	private void freeBoundaryPixel( final BoundaryPixel p )
	{
		reusableBoundaryPixels.push( p );
	}

	private final PartialComponent.Generator< T, C > componentGenerator;

	private final PartialComponent.Handler< C > componentOutput;

	private final Neighborhood neighborhood;

	private final RandomAccessible< BitType > visited;

	private final RandomAccess< BitType > visitedRandomAccess;

	private final PriorityQueue< BoundaryPixel > boundaryPixels;

	private final ArrayDeque< C > componentStack;

	private final Comparator< T > comparator;

	/**
	 * Set up data structures and run the algorithm. Completed components are
	 * emitted to the provided {@link PartialComponent.Handler}.
	 * 
	 * @param input
	 *            input image.
	 * @param componentGenerator
	 *            provides new {@link PartialComponent} instances.
	 * @param componentOutput
	 *            receives completed {@link PartialComponent}s.
	 * @param comparator
	 *            determines ordering of threshold values.
	 */
	private BuildComponentTree( final RandomAccessibleInterval< T > input, final PartialComponent.Generator< T, C > componentGenerator, final PartialComponent.Handler< C > componentOutput, final Comparator< T > comparator )
	{
		reusableBoundaryPixels = new ArrayDeque< BoundaryPixel >();
		this.componentGenerator = componentGenerator;
		this.componentOutput = componentOutput;

		final long[] dimensions = new long[ input.numDimensions() ];
		input.dimensions( dimensions );

		final ImgFactory< BitType > imgFactory = new ArrayImgFactory< BitType >();
		visited = imgFactory.create( dimensions, new BitType() );
		visitedRandomAccess = visited.randomAccess();

		neighborhood = new Neighborhood( dimensions );

		boundaryPixels = new PriorityQueue< BoundaryPixel >();

		componentStack = new ArrayDeque< C >();
		componentStack.push( componentGenerator.createMaxComponent() );

		this.comparator = comparator;

		run( input );
	}

	/**
	 * Main loop of the algorithm. This follows exactly along steps of the
	 * algorithm as described in the paper.
	 * 
	 * @param input
	 *            the input image.
	 */
	private void run( final RandomAccessibleInterval< T > input )
	{
		final RandomAccess< T > current = input.randomAccess();
		final RandomAccess< T > neighbor = input.randomAccess();
		input.min( current );
		neighbor.setPosition( current );
		visitedRandomAccess.setPosition( current );
		final T currentLevel = current.get().createVariable();
		final T neighborLevel = current.get().createVariable();

		// Note that step numbers in the comments below refer to steps in the
		// Nister & Stewenius paper.

		// step 2
		visitedRandomAccess.get().set( true );
		currentLevel.set( current.get() );

		// step 3
		componentStack.push( componentGenerator.createComponent( currentLevel ) );

		// step 4
		while ( true )
		{
			while ( neighborhood.hasNext() )
			{
				if ( !neighborhood.next( current, neighbor, visitedRandomAccess ) )
					continue;
				if ( !visitedRandomAccess.get().get() )
				{
					// actually we could
					// visit( neighbor );
					// here.
					// however, because wasVisited() already set the
					// visitedRandomAccess to the correct position, this is
					// faster:
					visitedRandomAccess.get().set( true );

					neighborLevel.set( neighbor.get() );
					if ( comparator.compare( neighborLevel, currentLevel ) >= 0 )
					{
						boundaryPixels.add( createBoundaryPixel( neighbor, neighborLevel, 0 ) );
					}
					else
					{
						boundaryPixels.add( createBoundaryPixel( current, currentLevel, neighborhood.getNextNeighborIndex() ) );
						current.setPosition( neighbor );
						currentLevel.set( neighborLevel );

						// go to 3, i.e.:
						componentStack.push( componentGenerator.createComponent( currentLevel ) );
						neighborhood.reset();
					}
				}
			}

			// step 5
			final C component = componentStack.peek();
			component.addPosition( current );

			// step 6
			if ( boundaryPixels.isEmpty() )
			{
				processStack( currentLevel );
				return;
			}

			final BoundaryPixel p = boundaryPixels.poll();
			if ( comparator.compare( p.get(), currentLevel ) != 0 )
			{
				// step 7
				processStack( p.get() );
			}
			current.setPosition( p );
			neighbor.setPosition( current );
			visitedRandomAccess.setPosition( current );
			currentLevel.set( p.get() );
			neighborhood.setNextNeighborIndex( p.getNextNeighborIndex() );
			freeBoundaryPixel( p );
		}
	}

	/**
	 * This is called whenever the current value is raised.
	 * 
	 * @param value
	 */
	private void processStack( final T value )
	{
		while ( true )
		{
			// process component on top of stack
			final C component = componentStack.pop();
			componentOutput.emit( component );

			// get level of second component on stack
			final C secondComponent = componentStack.peek();
			try
			{
				final int c = comparator.compare( value, secondComponent.getValue() );
				if ( c < 0 )
				{
					component.setValue( value );
					componentStack.push( component );
				}
				else
				{
					secondComponent.merge( component );
					if ( c > 0 )
						continue;
				}
				return;
			}
			catch ( final NullPointerException e )
			{
				componentStack.push( component );
				return;
			}
		}
	}
}
