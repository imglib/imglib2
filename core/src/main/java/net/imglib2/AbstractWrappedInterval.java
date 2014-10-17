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
package net.imglib2;

/**
 * Convenient base class for {@link IterableInterval IterableIntervals},
 * {@link RandomAccessibleInterval RandomAccessibleIntervals}, etc that forward
 * the {@link Interval} interface to, for example, their source accessible.
 * 
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public abstract class AbstractWrappedInterval< I extends Interval > extends AbstractWrappedRealInterval< I > implements Interval
{
	public AbstractWrappedInterval( final I source )
	{
		super( source );
	}

	@Override
	public void dimensions( final long[] dimensions )
	{
		sourceInterval.dimensions( dimensions );
	}

	@Override
	public long dimension( final int d )
	{
		return sourceInterval.dimension( d );
	}

	@Override
	public long min( final int d )
	{
		return sourceInterval.min( d );
	}

	@Override
	public void min( final long[] min )
	{
		sourceInterval.min( min );
	}

	@Override
	public void min( final Positionable min )
	{
		sourceInterval.min( min );
	}

	@Override
	public long max( final int d )
	{
		return sourceInterval.max( d );
	}

	@Override
	public void max( final long[] max )
	{
		sourceInterval.max( max );
	}

	@Override
	public void max( final Positionable max )
	{
		sourceInterval.max( max );
	}
}
