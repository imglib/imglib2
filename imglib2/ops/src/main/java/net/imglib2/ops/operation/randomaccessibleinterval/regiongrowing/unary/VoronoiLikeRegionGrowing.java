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
package net.imglib2.ops.operation.randomaccessibleinterval.regiongrowing.unary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.labeling.Labeling;
import net.imglib2.labeling.LabelingType;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.type.Type;
import net.imglib2.type.logic.BitType;

/**
 * 
 * @author hornm, University of Konstanz
 */
public class VoronoiLikeRegionGrowing< L extends Comparable< L >, T extends Type< T > & Comparable< T >> extends AbstractRegionGrowing< LabelingType< L >, L, Labeling< L >, Labeling< L >>
{

	private Cursor< LabelingType< L >> m_seedLabCur;

	private final RandomAccess< T > m_srcImgRA;

	private final T m_threshold;

	protected Img< T > m_srcImg;

	protected final boolean m_fillHoles;

	/**
	 * @param m_srcImg
	 * @param threshold
	 *            stops growing if the pixel value falls below that value
	 * @param fillHoles
	 *            fills the wholes in a post-processing step within segments of
	 *            the same label
	 */
	public VoronoiLikeRegionGrowing( Img< T > srcImg, T threshold, boolean fillHoles )
	{
		super( AbstractRegionGrowing.get8ConStructuringElement( srcImg.numDimensions() ), GrowingMode.SYNCHRONOUS, false );
		m_threshold = threshold;
		m_fillHoles = fillHoles;
		m_srcImgRA = srcImg.randomAccess();
		m_srcImg = srcImg;

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return
	 */
	@Override
	public Labeling< L > compute( Labeling< L > in, Labeling< L > out )
	{
		super.compute( in, out );
		// post-process the result
		if ( m_fillHoles )
		{
			reLabelBackgroundInsideCells( out );
		}

		return out;
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
		while ( m_seedLabCur.hasNext() )
		{
			m_seedLabCur.fwd();
			if ( !m_seedLabCur.get().getLabeling().isEmpty() )
			{
				m_seedLabCur.localize( seedPos );
				return m_seedLabCur.get().getLabeling().get( 0 );
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean includeInRegion( int[] oldPos, int[] nextPos, L label )
	{
		m_srcImgRA.setPosition( nextPos );
		return m_srcImgRA.get().compareTo( m_threshold ) >= 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void queueProcessed()
	{

	}

	/**
	 * Due to the nature of the segmentation algorithm, there may be background
	 * that lies completely inside cells. This background should be removed
	 * (e.g. cells extended to include that background).
	 */
	private void reLabelBackgroundInsideCells( Labeling< L > resLab )
	{

		Img< BitType > visited = null;
		try
		{
			visited = new ArrayImgFactory< BitType >().imgFactory( new BitType() ).create( resLab, new BitType() );
		}
		catch ( IncompatibleTypeException e )
		{
			e.printStackTrace();
		}

		Cursor< LabelingType< L >> labCur = resLab.localizingCursor();
		Cursor< BitType > visitedCur = visited.cursor();
		RandomAccess< BitType > visitedRA = visited.randomAccess();

		int[] pos = new int[ resLab.numDimensions() ];
		while ( labCur.hasNext() )
		{
			labCur.fwd();
			visitedCur.fwd();
			labCur.localize( pos );

			if ( !visitedCur.get().get() && labCur.get().getLabeling().isEmpty() )
			{
				bfsBackGround( resLab, pos, visitedRA );
			}

		}

	}

	/**
	 * Helper function for reLabelBackgroundInsideCells. Does a BF search
	 * starting from the given background point. If the contiguos background
	 * region only has 1 neighbour
	 * 
	 * @param yStart
	 *            y coordinate of starting point
	 * @param xStart
	 *            x coordinate of starting point
	 */
	private void bfsBackGround( Labeling< L > resLab, int[] startPos, RandomAccess< BitType > visitedRA )
	{
		Queue< int[] > queue = new LinkedList< int[] >();
		ArrayList< int[] > visitedNow = new ArrayList< int[] >();
		queue.add( startPos );
		visitedRA.setPosition( startPos );
		visitedRA.get().set( true );

		List< L > labelNeighbour = resLab.firstElement().getMapping().emptyList();
		boolean ok = true;
		RandomAccess< LabelingType< L >> resLabRA = resLab.randomAccess();
		int[] nextPos;
		int[] perm = new int[ resLab.numDimensions() ];
		while ( !queue.isEmpty() )
		{
			int[] pos = queue.remove();
			visitedNow.add( pos );

			Arrays.fill( perm, -1 );
			int i = resLab.numDimensions() - 1;
			boolean add;

			// iterate through the eight-connected-neighbourhood
			while ( i > -1 )
			{
				nextPos = pos.clone();
				add = true;
				// Modify position
				for ( int j = 0; j < resLab.numDimensions(); j++ )
				{
					nextPos[ j ] += perm[ j ];
					// Check boundaries
					if ( nextPos[ j ] < 0 || nextPos[ j ] >= resLab.dimension( j ) )
					{
						add = false;
						break;
					}
				}
				if ( add )
				{
					//
					visitedRA.setPosition( nextPos );
					resLabRA.setPosition( nextPos );
					if ( !visitedRA.get().get() && resLabRA.get().getLabeling().isEmpty() )
					{
						visitedRA.get().set( true );
						queue.add( nextPos.clone() );
					}
					else if ( !visitedRA.get().get() && !resLabRA.get().getLabeling().isEmpty() )
					{
						if ( labelNeighbour.isEmpty() )
						{
							labelNeighbour = resLabRA.get().getLabeling();
						}
						else if ( labelNeighbour != resLabRA.get().getLabeling() )
						{
							ok = false;
						}
					}
				}
				// Calculate next permutation
				for ( i = perm.length - 1; i > -1; i-- )
				{
					if ( perm[ i ] < 1 )
					{
						perm[ i ]++;
						for ( int j = i + 1; j < perm.length; j++ )
						{
							perm[ j ] = -1;
						}
						break;
					}
				}
			}
		}

		if ( ok && !labelNeighbour.isEmpty() )
		{
			for ( int i = 0; i < visitedNow.size(); ++i )
			{
				resLabRA.setPosition( visitedNow.get( i ) );
				resLabRA.get().setLabeling( labelNeighbour );
			}
		}
	}

	@Override
	public UnaryOperation< Labeling< L >, Labeling< L >> copy()
	{
		return new VoronoiLikeRegionGrowing< L, T >( m_srcImg, m_threshold.copy(), m_fillHoles );
	}

}
