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
 *   7 Dec 2011 (hornm): created
 */
package net.imglib2.ops.operation.randomaccessibleinterval.regiongrowing;

import java.util.Collection;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.labeling.Labeling;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.type.Type;

/**
 * First attempt to voronoi-like local region growing. NOT FINISHED YET!
 * 
 * @author hornm, University of Konstanz
 */
public class VoronoiLikeLocalRegionGrowing< L extends Comparable< L >, T extends Type< T > & Comparable< T >> extends VoronoiLikeRegionGrowing< L, T >
{

	private Img< T > m_srcImg;

	private RandomAccess< T > m_srcImgRA;

	/**
	 * @param m_srcImg
	 * @param threshold
	 *            stops growing if the pixel value falls below that value
	 * @param fillHoles
	 *            fills the wholes in a post-processing step within segments of
	 *            the same label
	 */
	public VoronoiLikeLocalRegionGrowing( Img< T > srcImg, boolean fillHoles )
	{
		super( srcImg, srcImg.firstElement().createVariable(), fillHoles );
		m_srcImg = srcImg;
		m_srcImgRA = m_srcImg.randomAccess();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initRegionGrowing( Labeling< L > srcLab )
	{
		super.initRegionGrowing( srcLab );

		// determine the local threshold for each region by means of the
		// seeding
		// regions
		Collection< L > labels = srcLab.getLabels();
		// Map<L, T> thresholdMap = new HashMap<L, T>();
		for ( L label : labels )
		{
			Cursor< T > cur = srcLab.getIterableRegionOfInterest( label ).getIterableIntervalOverROI( m_srcImg ).localizingCursor();
			while ( cur.hasNext() )
			{
				cur.fwd();
			}
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean includeInRegion( int[] oldPos, int[] nextPos, L label )
	{
		m_srcImgRA.setPosition( nextPos );
		return false;
	}

	@Override
	public UnaryOperation< Labeling< L >, Labeling< L >> copy()
	{
		return new VoronoiLikeLocalRegionGrowing< L, T >( m_srcImg, m_fillHoles );

	}
}
