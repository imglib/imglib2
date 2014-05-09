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
package net.imglib2.ops.operation.subset.views;

import net.imglib2.Cursor;
import net.imglib2.Sampler;
import net.imglib2.type.Type;

/**
 * {@link Cursor} wrapping another cursor. Iterates a given number of steps
 * (planeSize) from a given planePos.
 * 
 * @author Christian Dietz (University of Konstanz)
 * 
 * @param <T>
 * 
 * IMPORTANT: WILL BE INTEGRATED IN VIEW FRAMEWORK IN THE FUTURE
 * 
 */
class IterableSubsetViewCursor< T extends Type< T >> implements Cursor< T >
{

	private Cursor< T > cursor;

	private int m_typeIdx = 0;

	private int planePos;

	private int planeSize;

	private int numPlaneDims;

	protected IterableSubsetViewCursor( Cursor< T > cursor, int planeSize, int planePos, int numPlaneDims )
	{
		this.cursor = cursor;
		this.planeSize = planeSize;
		this.planePos = planePos;
		this.numPlaneDims = numPlaneDims;

		reset();
	}

	@Override
	public void localize( float[] position )
	{
		for ( int d = 0; d < numPlaneDims; d++ )
			position[ d ] = cursor.getFloatPosition( d );
	}

	@Override
	public void localize( double[] position )
	{
		for ( int d = 0; d < numPlaneDims; d++ )
			position[ d ] = cursor.getDoublePosition( d );
	}

	@Override
	public float getFloatPosition( int d )
	{
		return cursor.getFloatPosition( d );
	}

	@Override
	public double getDoublePosition( int d )
	{
		return cursor.getDoublePosition( d );
	}

	@Override
	public int numDimensions()
	{
		return numPlaneDims;
	}

	@Override
	public T get()
	{
		return cursor.get();
	}

	@Override
	public Sampler< T > copy()
	{
		return cursor.copy();
	}

	@Override
	public void jumpFwd( long steps )
	{
		cursor.jumpFwd( ( int ) steps );
		m_typeIdx += steps;
	}

	@Override
	public void fwd()
	{
		cursor.fwd();
		m_typeIdx++;
	}

	@Override
	public void reset()
	{
		cursor.reset();
		cursor.jumpFwd( planePos );
		m_typeIdx = -1;
	}

	@Override
	public boolean hasNext()
	{
		return m_typeIdx < planeSize - 1;
	}

	@Override
	public T next()
	{
		cursor.fwd();
		m_typeIdx++;

		return cursor.get();
	}

	@Override
	public void remove()
	{
		throw new UnsupportedOperationException( "Remove not supported in class: SubsetViewCursor" );
	}

	@Override
	public void localize( int[] position )
	{
		for ( int d = 0; d < numPlaneDims; d++ )
		{
			position[ d ] = cursor.getIntPosition( d );
		}
	}

	@Override
	public void localize( long[] position )
	{
		for ( int d = 0; d < numPlaneDims; d++ )
		{
			position[ d ] = cursor.getLongPosition( d );
		}

	}

	@Override
	public int getIntPosition( int d )
	{
		return cursor.getIntPosition( d );
	}

	@Override
	public long getLongPosition( int d )
	{
		return cursor.getLongPosition( d );
	}

	@Override
	public Cursor< T > copyCursor()
	{
		return new IterableSubsetViewCursor< T >( cursor.copyCursor(), planeSize, planePos, numPlaneDims );
	}
}
