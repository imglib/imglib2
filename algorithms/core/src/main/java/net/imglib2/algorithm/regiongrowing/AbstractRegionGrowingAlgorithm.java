/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */
package net.imglib2.algorithm.regiongrowing;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import net.imglib2.Dimensions;
import net.imglib2.RandomAccess;
import net.imglib2.algorithm.Benchmark;
import net.imglib2.algorithm.regiongrowing.RegionGrowingTools.GrowingMode;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.labeling.Labeling;
import net.imglib2.labeling.LabelingType;
import net.imglib2.labeling.NativeImgLabeling;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;

/**
 * Base class for region-growing algorithms.
 * <p>
 * This abstract class provides with the iteration, growing and labeling
 * behaviors. Concrete implementations have to provide:
 * <ul>
 * <li>Seed points, specified as a {@link Map} of labels to <code>long[]</code>.
 * <li>A {@link Labeling} of L, that will contain the region-growing result. See
 * {@link #initializeLabeling()}.
 * <li>A method for determining whether a candidate pixel should be added to
 * region currently under scrutiny. See
 * {@link #includeInRegion(long[], long[], Comparable)}. This method may depend
 * on the pixel value of a source image.
 * </ul>
 * <p>
 * Strongly inspired by Martin Horn (University of Konstanz) work in the
 * imglib2-ops package.
 * 
 * @author Jean-Yves Tinevez <jeanyves.tinevez@gmail.com> 2013
 * 
 * @param <L>
 */
public abstract class AbstractRegionGrowingAlgorithm< L extends Comparable< L > > implements RegionGrowingAlgorithm< L >, Benchmark
{

	private final GrowingMode growingMode;

	private final Map< L, List< L >> labelMap;

	private final boolean allowOverlap;

	/**
	 * A {@link RandomAccess} on a {@link Img} of {@link BitType} that stores
	 * whether a pixel has already been visited. This field is instantiated and
	 * used only if {@link #allowOverlap} is set to <code>false</code> at
	 * construction.
	 */
	private RandomAccess< BitType > visitedRandomAccess = null;

	/**
	 * A {@link RandomAccess} on a {@link LabelingType} that stores whether a
	 * pixel has already been considered as a candidate for all possible label.
	 * This field is initialized and used only if {@link #allowOverlap} is set
	 * to <code>true</code> at construction.
	 */
	private RandomAccess< LabelingType< L >> visitedByLabelsRandomAccess = null;

	protected final long[][] strel;

	protected Labeling< L > output;

	protected String errorMessage;

	protected long processingTime;

	private final Deque< long[] > seedQueue;

	protected final Map< long[], L > seedLabels;

	/**
	 * Base constructor for region-growing algorithms. This constructor
	 * specifies a few common parameters.
	 * 
	 * @param seedsLabels
	 *            a map that links seed positions (specified as
	 *            <code>long[]</code> arrays) to region labels.
	 * @param growingMode
	 *            the growing mode, whether synchronous or asynchronous.
	 * @param structuringElement
	 *            set of relative offsets defining the neighborhood of the
	 *            inspected pixel.
	 * @param allowOverlap
	 *            if <code>true</code>, then a single pixel can be part of
	 *            <b>several regions</b>. Otherwise, each pixel will be part of
	 *            at most one region (the first region that reaches it during
	 *            the calculation).
	 * @see GrowingMode
	 */
	public AbstractRegionGrowingAlgorithm( final Map< long[], L > seedsLabels, final GrowingMode growingMode, final long[][] structuringElement, final boolean allowOverlap )
	{
		this.seedLabels = seedsLabels;
		this.strel = structuringElement;
		this.growingMode = growingMode;
		this.allowOverlap = allowOverlap;
		this.labelMap = new HashMap< L, List< L >>();
		this.seedQueue = new LinkedList< long[] >();
		for ( final long[] seed : seedsLabels.keySet() )
		{
			seedQueue.add( seed );
		}
	}

	/*
	 * PRIVATE METHODS. They do the actual iteration and growing jobs.
	 */

	/**
	 * Calls one growth step on a single region.
	 * <p>
	 * The children of the specified parent positions are inspected, and used to
	 * create a list of successful child pixel position.
	 * 
	 * @param parentPixels
	 *            the parent pixels whose neighborhood is to inspect.
	 * @param label
	 *            the label of the current region.
	 * @param ra
	 *            the output {@link RandomAccess}, used to write the label onto
	 *            successful candidate positions.
	 * @param src
	 *            a Dimension, used to determine what candidate positions are
	 *            out of bounds.
	 * 
	 * @return a new {@link Queue} containing the pixel that have been added to
	 *         the current region. If the returned queue is empty, then the
	 *         region has grown to its final extent.
	 */
	private synchronized Queue< long[] > growProcess( final Iterator< long[] > parentPixels, final L label, final RandomAccess< LabelingType< L >> ra, final Dimensions src )
	{
		/*
		 * The queue, initially empty, that we will grow in this step. We will
		 * feed it with the children of the parent pixels found in the specified
		 * pixel queue.
		 */
		final Queue< long[] > newPixelQueue = new LinkedList< long[] >();

		while ( parentPixels.hasNext() )
		{
			// Get a pixel to grow.
			final long[] pixel = parentPixels.next();

			// Investigate its neighborhood.
			for ( final long[] offset : strel )
			{
				boolean outOfBounds = false;
				final long[] candidatePosition = pixel.clone();
				for ( int i = 0; i < pixel.length; i++ )
				{
					candidatePosition[ i ] = pixel[ i ] + offset[ i ];
					if ( candidatePosition[ i ] < 0 || candidatePosition[ i ] >= src.dimension( i ) )
					{
						outOfBounds = true;
						break;
					}
				}
				if ( !outOfBounds )
				{
					processPosition( candidatePosition, candidatePosition, newPixelQueue, label, ra );
				}
			}
		}
		finishedGrowStep( newPixelQueue, label );
		return newPixelQueue;
	}

	/**
	 * Processes a candidate position.
	 * <p>
	 * That is:
	 * <ul>
	 * <li>If it was visited, return.
	 * <li>Check if it fits candidate criterion. If not, return.
	 * <li>If yes, mark it with the specified label.
	 * <li>Mark it as visited.
	 * <li>Add it to the pixel queue.
	 * </ul>
	 * 
	 * @param parentPosition
	 *            the parent of the pixel to inspect.
	 * @param candidatePosition
	 *            the pixel to inspect.
	 * @param pixelQueue
	 *            the target queue for children pixels that have been
	 *            successfully added to the current region.
	 * @param label
	 *            the label of the current region.
	 * @param ra
	 *            the output {@link RandomAccess}, used to write the label onto
	 *            successful pixel candidates.
	 */
	private void processPosition( final long[] parentPosition, final long[] candidatePosition, final Queue< long[] > pixelQueue, final L label, final RandomAccess< LabelingType< L >> ra )
	{
		// Position the visited random access there.
		setVisitedPosition( candidatePosition );

		// If already visited, return.
		if ( wasVisitedBy( candidatePosition, label ) ) { return; }

		// If it does not match concrete implementation criterion, return.
		if ( !includeInRegion( parentPosition, candidatePosition, label ) ) { return; }

		// Mark position as processed
		markAsVisited( label );

		/*
		 * Add it to the children the pixel queue, that it will be processed in
		 * the next grow step.
		 */
		pixelQueue.add( candidatePosition );

		// Update the ra's position and write the label in the output.
		ra.setPosition( candidatePosition );
		setLabel( ra, label );
	}

	private void setVisitedPosition( final long[] pos )
	{
		if ( allowOverlap )
		{
			visitedByLabelsRandomAccess.setPosition( pos );
		}
		else
		{
			visitedRandomAccess.setPosition( pos );
		}
	}

	/**
	 * Returns whether the specified position has already been visited when
	 * growing the specified label.
	 * 
	 * @param position
	 *            the position to inspect.
	 * @param label
	 *            the label to query for. When overlapping is allowed, a pixel
	 *            might have been visited by a label, but not by another one.
	 * @return <code>true</code> if the position has been visited already.
	 */
	private boolean wasVisitedBy( final long[] position, final L label )
	{
		if ( allowOverlap )
		{
			return visitedByLabelsRandomAccess.get().getLabeling().contains( label );
		}
		else
		{
			return visitedRandomAccess.get().get();
		}
	}

	/**
	 * Marks the current position as visited by the specified label.
	 */
	private void markAsVisited( final L label )
	{
		if ( allowOverlap )
		{
			final List< L > l = new ArrayList< L >( visitedByLabelsRandomAccess.get().getLabeling() );
			l.add( label );
			visitedByLabelsRandomAccess.get().setLabeling( l );
		}
		else
		{
			visitedRandomAccess.get().set( true );
		}
	}

	/**
	 * Sets the label in the result labeling. To speed up it a bit, a map is
	 * used to get the already interned list of single labels.
	 */
	private void setLabel( final RandomAccess< LabelingType< L >> ra, final L label )
	{
		List< L > labeling;
		if ( ra.get().getLabeling().isEmpty() )
		{
			if ( ( labeling = labelMap.get( label ) ) == null )
			{
				// add the label and put the interned list into
				// the hash map
				labeling = new ArrayList< L >( 1 );
				labeling.add( label );
				labeling = ra.get().getMapping().intern( labeling );
			}
		}
		else
		{
			labeling = new ArrayList< L >( ra.get().getLabeling() );
			labeling.add( label );

		}
		ra.get().setLabeling( labeling );
	}

	/*
	 * OVERRIDEN METHODS
	 */

	@Override
	public boolean process()
	{
		final long start = System.currentTimeMillis();

		/*
		 * Get the output labeling.
		 */
		final Labeling< L > output = initializeLabeling();

		/*
		 * Image and random access to keep track of the already visited pixel
		 * positions. Depending on whether we authorize a pixel to belong to
		 * several regions, we have to use different strategies.
		 */
		if ( allowOverlap )
		{
			final NativeImgLabeling< L, IntType > tmp = new NativeImgLabeling< L, IntType >( new ArrayImgFactory< IntType >().create( output, new IntType() ) );
			visitedByLabelsRandomAccess = tmp.randomAccess();
		}
		else
		{
			final BitType bt = new BitType();
			Img< BitType > tmp = null;
			try
			{
				tmp = new ArrayImgFactory< BitType >().imgFactory( bt ).create( output, bt );
			}
			catch ( final IncompatibleTypeException e )
			{
				//
			}
			visitedRandomAccess = tmp.randomAccess();
		}

		// Access to the resulting labeling
		final RandomAccess< LabelingType< L >> outputRandomAccess = output.randomAccess();

		switch ( growingMode )
		{
		case SEQUENTIAL:
		{

			while ( !seedQueue.isEmpty() )
			{
				final long[] seed = seedQueue.pollFirst();
				final L label = seedLabels.get( seed );

				/*
				 * Instantiate the pixel queue for current label.
				 */
				Queue< long[] > pixelQueue = new LinkedList< long[] >();
				pixelQueue.add( seed );

				while ( true )
				{
					final Queue< long[] > childPixels = growProcess( pixelQueue.iterator(), label, outputRandomAccess, output );
					if ( childPixels.isEmpty() )
					{
						finishedLabel( label );
						break;
					}
					pixelQueue = childPixels;
				}
			}
			break;
		}

		case PARALLEL:
		{

			/*
			 * Prepare holder for parent pixels for each label.
			 */

			final Map< L, Queue< long[] >> parentPixels = new HashMap< L, Queue< long[] > >( seedLabels.size() );
			for ( final long[] seed : seedLabels.keySet() )
			{
				final Queue< long[] > queue = new LinkedList< long[] >();
				queue.add( seed );
				final L label = seedLabels.get( seed );
				parentPixels.put( label, queue );
			}

			/*
			 * Circle through seeds until we exhaust them.
			 */

			while ( !seedQueue.isEmpty() )
			{
				final Iterator< long[] > iterator = seedQueue.iterator();
				while ( iterator.hasNext() )
				{
					final long[] seed = iterator.next();
					final L label = seedLabels.get( seed );
					final Queue< long[] > queue = parentPixels.get( label );

					final Queue< long[] > childPixels = growProcess( queue.iterator(), label, outputRandomAccess, output );
					if ( childPixels.isEmpty() )
					{
						finishedLabel( label );
						// Remove the seed from the queue
						iterator.remove();
						parentPixels.remove( label );
						break;
					}
					else
					{
						parentPixels.put( label, childPixels );
					}

				}

			}
			break;
		}
		}

		final long end = System.currentTimeMillis();
		processingTime = end - start;
		return true;
	}

	@Override
	public boolean checkInput()
	{
		return true;
	}

	@Override
	public String getErrorMessage()
	{
		return errorMessage;
	}

	@Override
	public long getProcessingTime()
	{
		return processingTime;
	}

	@Override
	public Labeling< L > getResult()
	{
		return output;
	}
}
