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

package net.imglib2.display.projector.composite;

import java.util.ArrayList;

import net.imglib2.FinalInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converter;
import net.imglib2.display.projector.AbstractProjector2D;
import net.imglib2.type.numeric.ARGBType;

/**
 * Creates a composite image from across multiple dimensional positions along an
 * axis (typically, but not necessarily, channels). Each dimensional position
 * has its own {@link Converter}. The results of the conversions are summed into
 * the final value. Positions along the axis can be individually toggled for
 * inclusion in the computed composite value using the {@link #setComposite}
 * methods.
 *
 * See CompositeXYProjector for the code upon which this class was based.
 * See XYRandomAccessibleProjector for the code upon which this class was
 *      based.
 *
 * @author Stephan Saalfeld
 * @author Curtis Rueden
 * @author Grant Harris
 * @author Tobias Pietzsch
 */
public class CompositeXYRandomAccessibleProjector< A > extends AbstractProjector2D
{
	final protected RandomAccessibleInterval< ARGBType > target;

	private final ArrayList< Converter< A, ARGBType > > converters;

	private final int dimIndex;

	private final long positionCount;

	private final long positionMin;

	private final boolean[] composite;

	protected final long[] currentPositions;

	protected final Converter< A, ARGBType >[] currentConverters;

	protected final RandomAccessibleInterval< A > source;

	@SuppressWarnings( "unchecked" )
	public CompositeXYRandomAccessibleProjector( final RandomAccessibleInterval< A > source, final RandomAccessibleInterval< ARGBType > target, final ArrayList< Converter< A, ARGBType >> converters, final int dimIndex )
	{
		super( source.numDimensions() );
		this.source = source;
		this.target = target;
		this.converters = converters;
		this.dimIndex = dimIndex;

		// check that there is one converter per dimensional position
		positionCount = dimIndex < 0 ? 1 : source.dimension( dimIndex );
		positionMin = dimIndex < 0 ? 0 : source.min( dimIndex );
		final int converterCount = converters.size();
		if ( positionCount != converterCount ) { throw new IllegalArgumentException( "Expected " + positionCount + " converters but got " + converterCount ); }

		min[ dimIndex ] = source.min( dimIndex );
		max[ dimIndex ] = source.max( dimIndex );

		composite = new boolean[ converterCount ];
		composite[ 0 ] = true;
		currentPositions = new long[ converterCount ];
		currentConverters = new Converter[ converterCount ];
	}

	// -- CompositeXYProjector methods --

	/** Toggles the given position index's inclusion in composite values. */
	public void setComposite( final int index, final boolean on )
	{
		composite[ index ] = on;
	}

	/** Gets whether the given position index is included in composite values. */
	public boolean isComposite( final int index )
	{
		return composite[ index ];
	}

	/**
	 * Toggles composite mode globally. If true, all positions along the
	 * dimensional axis are included in the composite; if false, the value will
	 * consist of only the projector's current position (i.e., non-composite
	 * mode).
	 */
	public void setComposite( final boolean on )
	{
		for ( int i = 0; i < composite.length; i++ )
			composite[ i ] = on;
	}

	/** Gets whether composite mode is enabled for all positions. */
	public boolean isComposite()
	{
		for ( int i = 0; i < composite.length; i++ )
			if ( !composite[ i ] )
				return false;
		return true;
	}

	// -- Projector methods --

	@Override
	public void map()
	{
		for ( int d = 2; d < position.length; ++d )
			min[ d ] = max[ d ] = position[ d ];

		min[ 0 ] = target.min( 0 );
		min[ 1 ] = target.min( 1 );
		max[ 0 ] = target.max( 0 );
		max[ 1 ] = target.max( 1 );

		if ( dimIndex < 0 )
		{
			// there is only converter[0]
			// use it to map the current position
			final RandomAccess< A > sourceRandomAccess = source.randomAccess( new FinalInterval( min, max ) );
			sourceRandomAccess.setPosition( min );
			mapSingle( sourceRandomAccess, converters.get( 0 ) );
			return;
		}

		final int size = updateCurrentArrays();

		min[ dimIndex ] = max[ dimIndex ] = currentPositions[ 0 ];
		for ( int i = 1; i < size; ++i )
			if ( currentPositions[ i ] < min[ dimIndex ] )
				min[ dimIndex ] = currentPositions[ i ];
			else if ( currentPositions[ i ] > max[ dimIndex ] )
				max[ dimIndex ] = currentPositions[ i ];
		final RandomAccess< A > sourceRandomAccess = source.randomAccess( new FinalInterval( min, max ) );
		sourceRandomAccess.setPosition( min );

		if ( size == 1 )
		{
			// there is only one active converter: converter[0]
			// use it to map the slice at currentPositions[0]
			sourceRandomAccess.setPosition( currentPositions[ 0 ], dimIndex );
			mapSingle( sourceRandomAccess, currentConverters[ 0 ] );
			return;
		}

		final ARGBType bi = new ARGBType();

		final RandomAccess< ARGBType > targetRandomAccess = target.randomAccess();

		targetRandomAccess.setPosition( min[ 1 ], 1 );
		while ( targetRandomAccess.getLongPosition( 1 ) <= max[ 1 ] )
		{
			sourceRandomAccess.setPosition( min[ 0 ], 0 );
			targetRandomAccess.setPosition( min[ 0 ], 0 );
			while ( targetRandomAccess.getLongPosition( 0 ) <= max[ 0 ] )
			{
				int aSum = 0, rSum = 0, gSum = 0, bSum = 0;
				for ( int i = 0; i < size; i++ )
				{
					sourceRandomAccess.setPosition( currentPositions[ i ], dimIndex );
					currentConverters[ i ].convert( sourceRandomAccess.get(), bi );

					// accumulate converted result
					final int value = bi.get();
					final int a = ARGBType.alpha( value );
					final int r = ARGBType.red( value );
					final int g = ARGBType.green( value );
					final int b = ARGBType.blue( value );
					aSum += a;
					rSum += r;
					gSum += g;
					bSum += b;
				}
				if ( aSum > 255 )
					aSum = 255;
				if ( rSum > 255 )
					rSum = 255;
				if ( gSum > 255 )
					gSum = 255;
				if ( bSum > 255 )
					bSum = 255;
				targetRandomAccess.get().set( ARGBType.rgba( rSum, gSum, bSum, aSum ) );
				sourceRandomAccess.fwd( 0 );
				targetRandomAccess.fwd( 0 );
			}
			sourceRandomAccess.fwd( 1 );
			targetRandomAccess.fwd( 1 );
		}
	}

	// -- Helper methods --

	/**
	 * Walk through composite[] and store the currently active converters and
	 * positions (in dimension {@link #dimIndex}) to {@link #currentConverters}
	 * and {@link #currentPositions}.
	 *
	 * A special cases is single-position mode. The projector is in
	 * single-position mode iff all dimensional positions along the composited
	 * axis are excluded. In this case, the current position along that axis is
	 * used instead. The converter corresponding to the current position is
	 * used.
	 *
	 * @return number of positions to convert
	 */
	protected int updateCurrentArrays()
	{
		int currentSize = 0;
		for ( int i = 0; i < composite.length; i++ )
			if ( composite[ i ] )
				++currentSize;

		if ( currentSize == 0 )
		{
			// this is the isSingle() case.
			// map the current position using the converter at that position
			currentPositions[ 0 ] = position[ dimIndex ];
			currentConverters[ 0 ] = converters.get( ( int ) ( position[ dimIndex ] - positionMin ) );
			return 1;
		}
		// this is the normal case.
		// fill currentPositions and currentConverters with the active
		// positions and converters
		int j = 0;
		for ( int i = 0; i < composite.length; i++ )
			if ( composite[ i ] )
			{
				currentPositions[ j ] = positionMin + i;
				currentConverters[ j ] = converters.get( i );
				++j;
			}
		return currentSize;
	}

	protected void mapSingle( final RandomAccess< A > sourceRandomAccess, final Converter< A, ARGBType > conv )
	{
		final RandomAccess< ARGBType > targetRandomAccess = target.randomAccess();
		targetRandomAccess.setPosition( min[ 1 ], 1 );
		while ( targetRandomAccess.getLongPosition( 1 ) <= max[ 1 ] )
		{
			sourceRandomAccess.setPosition( min[ 0 ], 0 );
			targetRandomAccess.setPosition( min[ 0 ], 0 );
			while ( targetRandomAccess.getLongPosition( 0 ) <= max[ 0 ] )
			{
				conv.convert( sourceRandomAccess.get(), targetRandomAccess.get() );
				sourceRandomAccess.fwd( 0 );
				targetRandomAccess.fwd( 0 );
			}
			sourceRandomAccess.fwd( 1 );
			targetRandomAccess.fwd( 1 );
		}
	}
}
