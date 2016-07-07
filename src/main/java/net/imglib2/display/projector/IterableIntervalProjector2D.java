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
package net.imglib2.display.projector;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.FlatIterationOrder;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.converter.Converter;
import net.imglib2.view.RandomAccessibleIntervalCursor;
import net.imglib2.view.Views;

/**
 * A general 2D Projector that uses two dimensions as input to create the 2D
 * result. The output of the projection is written into a {@link IterableInterval}.
 * 
 * Depending on input and output an optimal strategy is chosen in the map() method.
 * 
 * Starting from the reference point two dimensions are sampled such
 * that a plain gets cut out of a higher dimensional data volume. <br>
 * The mapping function can be specified with a {@link Converter}. <br>
 * A basic example is cutting out a time frame from a (greyscale) video.
 * 
 * @author Michael Zinsmaier
 * @author Martin Horn
 * @author Christian Dietz (University of Konstanz)
 * 
 * @param <A>
 * @param <B>
 */
public class IterableIntervalProjector2D< A, B > extends AbstractProjector2D
{
	final protected Converter< ? super A, B > converter;

	final protected RandomAccessible< A > source;

	final protected IterableInterval< B > target;

	final int numDimensions;

	private final int dimX;

	private final int dimY;

	/**
	 * creates a new 2D projector that samples a plain in the dimensions dimX,
	 * dimY.
	 * 
	 * @param dimX
	 * @param dimY
	 * @param source
	 * @param target
	 * @param converter
	 *            a converter that is applied to each point in the plain. This
	 *            can e.g. be used for normalization, conversions, ...
	 */
	public IterableIntervalProjector2D( final int dimX, final int dimY, final RandomAccessible< A > source, final IterableInterval< B > target, final Converter< ? super A, B > converter )
	{
		super( source.numDimensions() );
		this.dimX = dimX;
		this.dimY = dimY;
		this.target = target;
		this.source = source;
		this.converter = converter;
		this.numDimensions = source.numDimensions();
	}

	/**
	 * projects data from the source to the target and applies the former
	 * specified {@link Converter} e.g. for normalization.
	 */
	@Override
	public void map()
	{
		// fix interval for all dimensions
		for ( int d = 0; d < position.length; ++d )
			min[ d ] = max[ d ] = position[ d ];

		min[ dimX ] = target.min( 0 );
		min[ dimY ] = target.min( 1 );
		max[ dimX ] = target.max( 0 );
		max[ dimY ] = target.max( 1 );

		// TODO: this is ugly, but the only way to make sure, that iteration
		// order fits in the case of one sized dims. Tobi?
		final IterableInterval< A > ii = Views.iterable( Views.interval( source, new FinalInterval( min, max ) ) );

		final Cursor< A > sourceCursor = ii.cursor();

		if ( target.iterationOrder().equals( ii.iterationOrder() ) && !( sourceCursor instanceof RandomAccessibleIntervalCursor ) )
		{
			final Cursor< B > targetCursor = target.cursor();
			while ( targetCursor.hasNext() )
			{
				converter.convert( sourceCursor.next(), targetCursor.next() );
			}
		}
		else if ( target.iterationOrder() instanceof FlatIterationOrder )
		{
			final Cursor< B > targetCursor = target.cursor();
			targetCursor.fwd();
			final FinalInterval sourceInterval = new FinalInterval( min, max );

			// use localizing cursor
			final RandomAccess< A > sourceRandomAccess = source.randomAccess( sourceInterval );
			sourceRandomAccess.setPosition( position );

			final long cr = -target.dimension( 0 );

			final long width = target.dimension( 0 );
			final long height = target.dimension( 1 );

			sourceRandomAccess.setPosition( min );
			for ( long y = 0; y < height; ++y )
			{
				for ( long x = 0; x < width; ++x )
				{
					converter.convert( sourceRandomAccess.get(), targetCursor.get() );
					sourceRandomAccess.fwd( dimX );
					targetCursor.fwd();
				}
				sourceRandomAccess.move( cr, dimX );
				sourceRandomAccess.fwd( dimY );
			}

		}
		else
		{
			final Cursor< B > targetCursor = target.localizingCursor();
			
			// use localizing cursor
			final RandomAccess< A > sourceRandomAccess = source.randomAccess();
			sourceRandomAccess.setPosition( position );
			while ( targetCursor.hasNext() )
			{
				final B b = targetCursor.next();
				sourceRandomAccess.setPosition( targetCursor.getLongPosition( 0 ), dimX );
				sourceRandomAccess.setPosition( targetCursor.getLongPosition( 1 ), dimY );

				converter.convert( sourceRandomAccess.get(), b );
			}
		}
	}
}
