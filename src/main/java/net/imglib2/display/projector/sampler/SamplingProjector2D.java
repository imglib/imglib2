/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
package net.imglib2.display.projector.sampler;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converter;
import net.imglib2.display.projector.AbstractProjector2D;

/**
 * A general 2D Projector that uses three dimensions as input to create the 2D
 * result. Starting from the reference point (see {@link AbstractProjector2D})
 * two dimensions are sampled such that a plain gets cut out of a higher
 * dimensional data volume. The third dimension is projected (in a mathematical
 * sense) onto this plain. <br>
 * The mapping function is specified by a {@link Converter}. It is not necessary
 * to process the complete interval of the third dimension, instead
 * {@link ProjectedSampler} can be used to control the sampling. <br>
 * A basic example is cutting out the x,y plain and projecting the color
 * dimension onto the plain. Alternatively mapping up to three measures (from a
 * measurement dimension) to the three color channels would also be possible...
 *
 * @author Michael Zinsmaier
 * @author Martin Horn
 * @author Christian Dietz
 *
 * @param <A>
 *            source type
 * @param <B>
 *            target type
 */
public class SamplingProjector2D< A, B > extends AbstractProjector2D
{

	private final static int X = 0;

	private final static int Y = 1;

	protected final Converter< ProjectedSampler< A >, B > converter;

	protected final IterableInterval< B > target;

	protected final RandomAccessible< A > source;

	protected final int dimX;

	protected final int dimY;

	private final int projectedDimension;

	private final ProjectedSampler< A > projectionSampler;

	// min and max of the USED part of the projected dimension
	private long projectedDimMinPos;

	private long projectedDimMaxPos;

	/**
	 *
	 * @param dimX
	 *            the x dimension of the created plain
	 * @param dimY
	 *            the y dimension of the created plain
	 * @param source
	 * @param target
	 * @param converter
	 *            a special converter that uses {@link ProjectedSampler} to
	 *            process values from the third dimension (multiple values
	 *            selected by the ProjectedDimSampler get converted to a new
	 *            value in the resulting 2D dataset e.g. color channel to int
	 *            color)
	 * @param projectedDimension
	 *            selection of the third dimension
	 * @param projectedPositions
	 */
	public SamplingProjector2D( final int dimX, final int dimY, final RandomAccessible< A > source, final IterableInterval< B > target, final Converter< ProjectedSampler< A >, B > converter, final int projectedDimension, final long[] projectedPositions )
	{

		super( source.numDimensions() );

		this.dimX = dimX;
		this.dimY = dimY;
		this.target = target;
		this.source = source;
		this.converter = converter;
		this.projectedDimension = projectedDimension;

		// get min and max of the USED part of the projection dim
		projectedDimMinPos = Long.MAX_VALUE;
		projectedDimMaxPos = Long.MIN_VALUE;
		for ( final long pos : projectedPositions )
		{
			if ( pos < projectedDimMinPos )
			{
				projectedDimMinPos = pos;
			}
			if ( pos > projectedDimMaxPos )
			{
				projectedDimMaxPos = pos;
			}
		}

		projectionSampler = new SelectiveSampler< A >( projectedDimension, projectedPositions );
	}

	public SamplingProjector2D( final int dimX, final int dimY, final RandomAccessibleInterval< A > source, final IterableInterval< B > target, final Converter< ProjectedSampler< A >, B > converter, final int projectedDimension )
	{

		super( source.numDimensions() );

		this.dimX = dimX;
		this.dimY = dimY;
		this.target = target;
		this.source = source;
		this.converter = converter;
		this.projectedDimension = projectedDimension;

		// set min and max of the projection dim
		projectedDimMinPos = source.min( projectedDimension );
		projectedDimMaxPos = source.max( projectedDimension );

		projectionSampler = new IntervalSampler< A >( projectedDimension, projectedDimMinPos, projectedDimMaxPos );
	}

	@Override
	public void map()
	{
		// fix interval for all dimensions
		for ( int d = 0; d < position.length; ++d )
			min[ d ] = max[ d ] = position[ d ];

		min[ dimX ] = target.min( X );
		min[ dimY ] = target.min( Y );
		max[ dimX ] = target.max( X );
		max[ dimY ] = target.max( Y );
		min[ projectedDimension ] = projectedDimMinPos;
		max[ projectedDimension ] = projectedDimMaxPos;

		// get tailored random access
		final FinalInterval sourceInterval = new FinalInterval( min, max );
		final Cursor< B > targetCursor = target.localizingCursor();
		final RandomAccess< A > sourceRandomAccess = source.randomAccess( sourceInterval );
		sourceRandomAccess.setPosition( position );

		projectionSampler.setRandomAccess( sourceRandomAccess );

		if ( n > 1 )
			while ( targetCursor.hasNext() )
			{
				projectionSampler.reset();

				final B b = targetCursor.next();
				sourceRandomAccess.setPosition( targetCursor.getLongPosition( X ), dimX );
				sourceRandomAccess.setPosition( targetCursor.getLongPosition( Y ), dimY );

				converter.convert( projectionSampler, b );
			}
		else
			while ( targetCursor.hasNext() )
			{
				projectionSampler.reset();

				final B b = targetCursor.next();
				sourceRandomAccess.setPosition( targetCursor.getLongPosition( X ), dimX );

				converter.convert( projectionSampler, b );
			}
	}

}
