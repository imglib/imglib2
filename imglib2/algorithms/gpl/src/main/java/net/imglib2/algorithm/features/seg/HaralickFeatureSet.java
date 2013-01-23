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
 *   20 Sep 2010 (hornm): created
 */
package net.imglib2.algorithm.features.seg;

import java.util.BitSet;

import net.imglib2.IterableInterval;
import net.imglib2.algorithm.features.FeatureSet;
import net.imglib2.algorithm.features.FeatureTargetListener;
import net.imglib2.algorithm.features.ObjectCalcAndCache;
import net.imglib2.algorithm.features.SharesObjects;
import net.imglib2.ops.data.CooccurrenceMatrix;
import net.imglib2.ops.data.CooccurrenceMatrix.MatrixOrientation;
import net.imglib2.ops.operation.iterableinterval.unary.MakeCooccurrenceMatrix.HaralickFeature;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.ValuePair;

/**
 * Implementation of Haralick Feature Set
 * 
 * @author dietzc, hornm, selliens University of Konstanz
 */
public class HaralickFeatureSet< T extends RealType< T >> implements FeatureSet, SharesObjects
{

	private final int m_distance;

	private final int m_nrGrayLevels;

	private final MatrixOrientation m_matrixOrientation;

	private ObjectCalcAndCache m_ocac;

	private final BitSet enabledFeatures = new BitSet( numFeatures() );

	private IterableInterval< T > m_interval;

	private ValuePair< Integer, Integer > m_validDims;

	private int m_dimX;

	private int m_dimY;

	private boolean m_isValid;

	/**
	 * @param nrGrayLevels
	 * @param distance
	 * @param target
	 */
	public HaralickFeatureSet( int nrGrayLevels, int distance, MatrixOrientation orientation )
	{
		super();
		m_nrGrayLevels = nrGrayLevels;
		m_distance = distance;
		m_matrixOrientation = orientation;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double value( int id )
	{

		if ( !m_isValid )
			return Double.NaN;

		CooccurrenceMatrix coocMat = m_ocac.cooccurenceMatrix( m_interval, m_dimX, m_dimY, m_distance, m_nrGrayLevels, m_matrixOrientation, enabledFeatures );

		return coocMat.getFeature( id );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name( int id )
	{
		return HaralickFeature.values()[ id ].toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int numFeatures()
	{
		return HaralickFeature.values().length;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String featureSetId()
	{
		return "Haralick Feature Factory";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void enable( int id )
	{
		enabledFeatures.set( id );
	}

	@FeatureTargetListener
	public final void iiUpdated( IterableInterval< T > interval )
	{

		m_validDims = getValidDims( interval );

		if ( m_validDims != null )
		{
			m_isValid = true;
			m_interval = interval;
			m_dimX = m_validDims.a;
			m_dimY = m_validDims.b;
		}
		else
		{
			m_isValid = false;
		}

	}

	private ValuePair< Integer, Integer > getValidDims( IterableInterval< T > interval )
	{

		int dimX = -1;
		int dimY = -1;

		for ( int d = 0; d < interval.numDimensions(); d++ )
			if ( interval.dimension( d ) > 1 )
				if ( dimX < 0 )
					dimX = d;
				else if ( dimY < 0 )
					dimY = d;
				else
					return null;

		if ( dimX < 0 || dimY < 0 )
			return null;

		return new ValuePair< Integer, Integer >( dimX, dimY );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class< ? >[] getSharedObjectClasses()
	{
		return new Class[] { ObjectCalcAndCache.class };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSharedObjectInstances( Object[] instances )
	{
		m_ocac = ( ObjectCalcAndCache ) instances[ 0 ];

	}

}
