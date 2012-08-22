/*
 * ------------------------------------------------------------------------
 *
 *  Copyright (C) 2003 - 2010
 *  University of Konstanz, Germany and
 *  KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME. The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 *
 * History
 *   9 Dec 2011 (hornm): created
 */
package net.imglib2.ops.operation.labeling.unary;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.imglib2.Cursor;
import net.imglib2.labeling.Labeling;
import net.imglib2.labeling.LabelingType;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.ops.operation.randomaccessibleinterval.regiongrowing.AbstractRegionGrowing;
import net.imglib2.ops.operation.randomaccessibleinterval.regiongrowing.AbstractRegionGrowing.GrowingMode;
import net.imglib2.util.Pair;

/**
 * 
 * @author hornm, University of Konstanz
 */
public class GrowLabeling< L extends Comparable< L >> extends AbstractRegionGrowing< LabelingType< L >, L, Labeling< L >, Labeling< L >>
{

	private Cursor< LabelingType< L >> m_seedLabCur;

	private final List< Pair< int[], L >> m_seedingPoints = new ArrayList< Pair< int[], L >>();

	private Iterator< Pair< int[], L >> m_seedIterator;

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
	protected void initRegionGrowing( Labeling< L > srcImg )
	{
		m_seedLabCur = srcImg.localizingCursor();

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
				Pair< int[], L > next = m_seedIterator.next();
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
		m_seedingPoints.add( new Pair< int[], L >( nextPos, label ) );
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
	public UnaryOperation< Labeling< L >, Labeling< L >> copy()
	{
		return new GrowLabeling< L >( m_structuringElement.clone(), m_iterations );
	}

}
