/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2023 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.stream;

import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.RealPositionable;
import net.imglib2.type.Index;
import net.imglib2.util.IntervalIndexer;
import net.imglib2.util.Util;

/**
 * TODO javadoc
 * @author Tobias Pietzsch
 */
// TODO: Move to net.imglib2.img.array? Move to net.imglib2?
// TODO: doesn't have to be abstract. Rename to BaseIndexLocalizable, IndexLocalizable ?
public abstract class AbstractIndexLocalizable implements Localizable
{
	private final Index index;

	private final int[] dimensions;

	private final int[] steps;

	public AbstractIndexLocalizable( Index index, int[] dimensions, int[] steps )
	{
		this.index = index;
		this.dimensions = dimensions;
		this.steps = steps;
	}

	protected Index index()
	{
		return index;
	}

	@Override
	public int numDimensions()
	{
		return dimensions.length;
	}

	@Override
	public void localize( final int[] position )
	{
		IntervalIndexer.indexToPosition( index.get(), dimensions, position );
	}

	@Override
	public void localize( final float[] position )
	{
		IntervalIndexer.indexToPosition( index.get(), dimensions, position );
	}

	@Override
	public void localize( final double[] position )
	{
		IntervalIndexer.indexToPosition( index.get(), dimensions, position );
	}

	@Override
	public void localize( final long[] position )
	{
		IntervalIndexer.indexToPosition( index.get(), dimensions, position );
	}

	@Override
	public void localize( final Positionable position )
	{
		IntervalIndexer.indexToPosition( index.get(), dimensions, position );
	}

	@Override
	public void localize( final RealPositionable position )
	{
		IntervalIndexer.indexToPosition( index.get(), dimensions, position );
	}

	@Override
	public int getIntPosition( final int d )
	{
		if ( d == 0 )
			return index.get() % dimensions[ 0 ];
		else
			return IntervalIndexer.indexToPosition( index.get(), dimensions, steps, d );
	}

	@Override
	public long getLongPosition( final int d )
	{
		return getIntPosition( d );
	}

	@Override
	public float getFloatPosition( final int d )
	{
		return getIntPosition( d );
	}

	@Override
	public double getDoublePosition( final int d )
	{
		return getIntPosition( d );
	}

	@Override
	public String toString()
	{
		// TODO: Implement static Localizable.toString( Localizable )
		//       Use StringBuilder or StringJoiner.
		return Util.printCoordinates( this );
	}
}
