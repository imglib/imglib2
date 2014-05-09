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
package net.imglib2.ops.operation.labeling.unary;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.labeling.Labeling;
import net.imglib2.labeling.LabelingType;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.ops.operation.randomaccessibleinterval.unary.regiongrowing.AbstractRegionGrowing;
import net.imglib2.util.ValuePair;
import net.imglib2.view.Views;

/**
 * 
 * @author Christian Dietz (University of Konstanz)
 */
public class GrowLabeling< L extends Comparable< L >> extends AbstractRegionGrowing< LabelingType< L >, L>
{

	private Cursor< LabelingType< L >> m_seedLabCur;

	private final List< ValuePair< int[], L >> m_seedingPoints = new ArrayList< ValuePair< int[], L >>();

	private Iterator< ValuePair< int[], L >> m_seedIterator;

	private boolean m_initSeeds = true;

	private final int m_numIterations;

	private int m_iterations = 0;

	/**
	 * @param structuringElement
	 * @param numIterations
	 */
	public GrowLabeling( long[][] structuringElement, int numIterations )
	{
		super( structuringElement, GrowingMode.SYNCHRONOUS, true );
		m_numIterations = numIterations;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initRegionGrowing( RandomAccessibleInterval< LabelingType < L > > srcImg )
	{
		m_seedLabCur = Views.iterable(srcImg).localizingCursor();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected L nextSeedPosition( int[] seedPos )
	{
		if ( m_initSeeds )
		{
			while ( m_seedLabCur.hasNext() )
			{
				m_seedLabCur.fwd();
				if ( !m_seedLabCur.get().getLabeling().isEmpty() )
				{
					m_seedLabCur.localize( seedPos );
					return m_seedLabCur.get().getLabeling().get( 0 );
				}
			}
		}
		else
		{
			if ( m_seedIterator.hasNext() )
			{
				ValuePair< int[], L > next = m_seedIterator.next();
				for ( int i = 0; i < seedPos.length; i++ )
				{
					seedPos[ i ] = next.a[ i ];
				}
				return next.b;
			}
		}
		m_seedingPoints.clear();
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean includeInRegion( int[] oldPos, int[] nextPos, L label )
	{
		m_seedingPoints.add( new ValuePair< int[], L >( nextPos, label ) );
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void queueProcessed()
	{
		// Nothing to do here
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean hasMoreSeedingPoints()
	{
		m_initSeeds = false;
		m_seedIterator = m_seedingPoints.iterator();
		return m_iterations++ < m_numIterations;
	}

	@Override
	public UnaryOperation< RandomAccessibleInterval< LabelingType < L > >, Labeling< L >> copy()
	{
		return new GrowLabeling< L >( m_structuringElement.clone(), m_iterations );
	}

}
