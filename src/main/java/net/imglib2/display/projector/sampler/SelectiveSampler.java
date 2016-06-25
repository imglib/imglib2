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

import net.imglib2.RandomAccess;
import net.imglib2.Sampler;

/**
 * Provides access to a set of predefined positions of a projected dimension
 * 
 * @author Michael Zinsmaier
 * @author Martin Horn
 * @author Christian Dietz (University of Konstanz)
 * 
 * @param <T>
 */
public class SelectiveSampler< T > implements ProjectedSampler< T >
{

	private final long[] m_projectedPositions;

	private final int m_projectionDimension;

	private RandomAccess< T > m_source;

	private int m_selectedIndex = 0;

	public SelectiveSampler( final int projectionDimension, final long[] projectedPositions )
	{
		m_projectedPositions = projectedPositions;
		m_projectionDimension = projectionDimension;
	}

	@Override
	public void jumpFwd( final long steps )
	{
		m_selectedIndex += steps;
	}

	@Override
	public void fwd()
	{
		m_selectedIndex++;
	}

	@Override
	public void reset()
	{
		m_selectedIndex = 0;
	}

	@Override
	public boolean hasNext()
	{
		return ( m_selectedIndex < m_projectedPositions.length );
	}

	@Override
	public T get()
	{
		m_source.setPosition( m_selectedIndex, m_projectionDimension );
		return m_source.get();
	}

	@Override
	public Sampler< T > copy()
	{
		m_source.setPosition( m_selectedIndex, m_projectionDimension );
		return m_source.copy();
	}

	@Override
	public void setRandomAccess( final RandomAccess< T > srcAccess )
	{
		m_source = srcAccess;
	}
}
