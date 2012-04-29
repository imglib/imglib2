/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
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

package net.imglib2.converter;

import java.util.Iterator;

import net.imglib2.IterableInterval;
import net.imglib2.IterableRealInterval;
import net.imglib2.Positionable;
import net.imglib2.RealPositionable;

/**
 * TODO
 *
 */
abstract public class AbstractConvertedIterableInterval< A, B > implements IterableInterval< B >
{
	final protected IterableInterval< A > source;

	public AbstractConvertedIterableInterval( final IterableInterval< A > source )
	{
		this.source = source;
	}

	@Override
	public int numDimensions()
	{
		return source.numDimensions();
	}

	@Override
	public long min( final int d )
	{
		return source.min( d );
	}

	@Override
	public void min( final long[] min )
	{
		source.min( min );
	}

	@Override
	public void min( final Positionable min )
	{
		source.min( min );
	}

	@Override
	public long max( final int d )
	{
		return source.max( d );
	}

	@Override
	public void max( final long[] max )
	{
		source.max( max );
	}

	@Override
	public void max( final Positionable max )
	{
		source.max( max );
	}

	@Override
	public void dimensions( final long[] dimensions )
	{
		source.dimensions( dimensions );
	}

	@Override
	public long dimension( final int d )
	{
		return source.dimension( d );
	}

	@Override
	public double realMin( final int d )
	{
		return source.realMin( d );
	}

	@Override
	public void realMin( final double[] min )
	{
		source.realMin( min );
	}

	@Override
	public void realMin( final RealPositionable min )
	{
		source.realMin( min );
	}

	@Override
	public double realMax( final int d )
	{
		return source.realMax( d );
	}

	@Override
	public void realMax( final double[] max )
	{
		source.realMax( max );
	}

	@Override
	public void realMax( final RealPositionable max )
	{
		source.realMax( max );
	}

	@Override
	public long size()
	{
		return source.size();
	}

	@Override
	public Object iterationOrder()
	{
		return source.iterationOrder();
	}

	@Override
	public boolean equalIterationOrder( final IterableRealInterval< ? > f )
	{
		return iterationOrder().equals( f.iterationOrder() );
	}

	@Override
	public Iterator< B > iterator()
	{
		return cursor();
	}

	@Override
	public B firstElement()
	{
		return cursor().next();
	}

	@Override
	abstract public AbstractConvertedCursor< A, B > cursor();

	@Override
	abstract public AbstractConvertedCursor< A, B > localizingCursor();
}
