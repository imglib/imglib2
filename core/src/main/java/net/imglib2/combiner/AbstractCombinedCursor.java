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

package net.imglib2.combiner;

import net.imglib2.Cursor;

/**
 * TODO
 * 
 */
abstract public class AbstractCombinedCursor< A, B, C > implements Cursor< C >
{
	final protected Cursor< A > sourceA;

	final protected Cursor< B > sourceB;

	public AbstractCombinedCursor( final Cursor< A > sourceA, final Cursor< B > sourceB )
	{
		this.sourceA = sourceA;
		this.sourceB = sourceB;

	}

	@Override
	public void localize( final int[] position )
	{
		sourceA.localize( position );
	}

	@Override
	public void localize( final long[] position )
	{
		sourceA.localize( position );
	}

	@Override
	public int getIntPosition( final int d )
	{
		return sourceA.getIntPosition( d );
	}

	@Override
	public long getLongPosition( final int d )
	{
		return sourceA.getLongPosition( d );
	}

	@Override
	public void localize( final float[] position )
	{
		sourceA.localize( position );
	}

	@Override
	public void localize( final double[] position )
	{
		sourceA.localize( position );
	}

	@Override
	public float getFloatPosition( final int d )
	{
		return sourceA.getFloatPosition( d );
	}

	@Override
	public double getDoublePosition( final int d )
	{
		return sourceA.getDoublePosition( d );
	}

	@Override
	public int numDimensions()
	{
		return sourceA.numDimensions();
	}

	@Override
	public void jumpFwd( final long steps )
	{
		sourceA.jumpFwd( steps );
	}

	@Override
	public void fwd()
	{
		sourceA.fwd();
	}

	@Override
	public void reset()
	{
		sourceA.reset();
	}

	@Override
	public boolean hasNext()
	{
		return sourceA.hasNext();
	}

	@Override
	public C next()
	{
		fwd();
		return get();
	}

	@Override
	public void remove()
	{
		sourceA.remove();
	}

	@Override
	abstract public AbstractCombinedCursor< A, B, C > copy();

	@Override
	public AbstractCombinedCursor< A, B, C > copyCursor()
	{
		return copy();
	}
}
