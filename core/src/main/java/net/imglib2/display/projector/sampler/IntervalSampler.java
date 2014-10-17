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
package net.imglib2.display.projector.sampler;

import net.imglib2.RandomAccess;
import net.imglib2.Sampler;

/**
 * allows stepwise access to a preselected projected dimension.
 * 
 * @author Michael Zinsmaier, Martin Horn, Christian Dietz
 * 
 * @param <T>
 */
public class IntervalSampler< T > implements ProjectedSampler< T >
{

	private final int m_projectionDimension;

	private final long m_startPosition;

	private final long m_endPosition;

	private RandomAccess< T > m_source;

	public IntervalSampler( final int projectionDimension, final long startPosition, final long endPosition )
	{

		m_projectionDimension = projectionDimension;
		m_startPosition = startPosition;
		m_endPosition = endPosition;
	}

	@Override
	public void jumpFwd( final long steps )
	{
		for ( int i = 0; i < steps; i++ )
		{
			fwd();
		}
	}

	@Override
	public void fwd()
	{
		m_source.fwd( m_projectionDimension );
	}

	@Override
	public void reset()
	{
		m_source.setPosition( m_startPosition, m_projectionDimension );
	}

	@Override
	public boolean hasNext()
	{
		return ( m_source.getLongPosition( m_projectionDimension ) <= m_endPosition );
	}

	@Override
	public T get()
	{
		return m_source.get();
	}

	@Override
	public Sampler< T > copy()
	{
		return m_source.copy();
	}

	@Override
	public void setRandomAccess( final RandomAccess< T > srcAccess )
	{
		m_source = srcAccess;
	}

}
