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

import net.imglib2.FinalInterval;
import net.imglib2.FlatIterationOrder;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converter;

/**
 * A general 2D Projector that uses two dimensions as input to create the 2D
 * result. The output of the projection is written into a
 * {@link RandomAccessibleInterval}.
 * 
 * Starting from the reference point two dimensions are sampled such that a
 * plain gets cut out of a higher dimensional data volume. <br>
 * The mapping function can be specified with a {@link Converter}. <br>
 * A basic example is cutting out a time frame from a (greyscale) video
 * 
 * Note: This {@link Projector} should only be used if one can be sure, that the
 * target {@link RandomAccessibleInterval} is not an {@link IterableInterval} of
 * {@link FlatIterationOrder}. If, please use
 * {@link IterableIntervalProjector2D} for performance reasons.
 * 
 * @author Michael Zinsmaier
 * @author Martin Horn
 * @author Christian Dietz (University of Konstanz)
 * 
 * @param <A>
 * @param <B>
 */
public class RandomAccessibleProjector2D< A, B > extends AbstractProjector2D
{

	final protected Converter< ? super A, B > converter;

	final protected RandomAccessibleInterval< B > target;

	final protected RandomAccessible< A > source;

	final int numDimensions;

	private final int dimX;

	private final int dimY;

	protected final int X = 0;

	protected final int Y = 1;

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
	public RandomAccessibleProjector2D( final int dimX, final int dimY, final RandomAccessible< A > source, final RandomAccessibleInterval< B > target, final Converter< ? super A, B > converter )
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
		for ( int d = 2; d < position.length; ++d )
			min[ d ] = max[ d ] = position[ d ];

		min[ dimX ] = target.min( dimX );
		min[ dimY ] = target.min( dimY );
		max[ dimX ] = target.max( dimX );
		max[ dimY ] = target.max( dimY );
		final FinalInterval sourceInterval = new FinalInterval( min, max );

		final long cr = -target.dimension( dimX );

		final RandomAccess< B > targetRandomAccess = target.randomAccess( target );
		final RandomAccess< A > sourceRandomAccess = source.randomAccess( sourceInterval );

		final long width = target.dimension( dimX );
		final long height = target.dimension( dimY );

		sourceRandomAccess.setPosition( min );
		targetRandomAccess.setPosition( min[ dimX ], dimX );
		targetRandomAccess.setPosition( min[ dimY ], dimY );
		for ( long y = 0; y < height; ++y )
		{
			for ( long x = 0; x < width; ++x )
			{
				converter.convert( sourceRandomAccess.get(), targetRandomAccess.get() );
				sourceRandomAccess.fwd( dimX );
				targetRandomAccess.fwd( dimX );
			}
			sourceRandomAccess.move( cr, dimX );
			targetRandomAccess.move( cr, dimX );
			sourceRandomAccess.fwd( dimY );
			targetRandomAccess.fwd( dimY );
		}
	}
}
