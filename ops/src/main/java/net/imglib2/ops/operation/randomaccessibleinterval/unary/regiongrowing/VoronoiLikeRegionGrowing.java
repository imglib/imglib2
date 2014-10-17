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

package net.imglib2.ops.operation.randomaccessibleinterval.unary.regiongrowing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.labeling.Labeling;
import net.imglib2.labeling.LabelingType;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.type.Type;
import net.imglib2.type.logic.BitType;
import net.imglib2.view.Views;

/**
 * @author Martin Horn (University of Konstanz)
 */
public class VoronoiLikeRegionGrowing< L extends Comparable< L >, T extends Type< T > & Comparable< T >> extends AbstractRegionGrowing< LabelingType< L >, L >
{

	private Cursor< LabelingType< L >> m_seedLabCur;

	private final RandomAccess< T > m_srcImgRA;

	private final T m_threshold;

	protected RandomAccessibleInterval< T > m_srcImg;

	protected final boolean m_fillHoles;

	/**
	 * TODO
	 * 
	 * @param srcImg
	 *            TODO
	 * @param threshold
	 *            stops growing if the pixel value falls below that value
	 * @param fillHoles
	 *            fills the wholes in a post-processing step within segments of
	 *            the same label
	 */
	public VoronoiLikeRegionGrowing( RandomAccessibleInterval< T > srcImg, T threshold, boolean fillHoles )
	{
		super( AbstractRegionGrowing.get8ConStructuringElement( srcImg.numDimensions() ), GrowingMode.SYNCHRONOUS, false );
		m_threshold = threshold;
		m_fillHoles = fillHoles;
		m_srcImgRA = srcImg.randomAccess();
		m_srcImg = srcImg;

	}

	@Override
	public Labeling< L > compute( RandomAccessibleInterval< LabelingType< L > > in, Labeling< L > out )
	{
		super.compute( in, out );
		// post-process the result
		if ( m_fillHoles )
		{
			reLabelBackgroundInsideCells( out );
		}

		return out;
	}

	@Override
	protected void initRegionGrowing( RandomAccessibleInterval< LabelingType< L > > srcImg )
	{
		m_seedLabCur = Views.iterable( srcImg ).localizingCursor();

	}

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

	@Override
	protected boolean includeInRegion( int[] oldPos, int[] nextPos, L label )
	{
		m_srcImgRA.setPosition( nextPos );
		return m_srcImgRA.get().compareTo( m_threshold ) >= 0;
	}

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
	 * @param resLab
	 *            TODO
	 * @param startPos
	 *            coordinates of starting point
	 * @param visitedRA
	 *            TODO
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
	public UnaryOperation< RandomAccessibleInterval< LabelingType< L > >, Labeling< L >> copy()
	{
		return new VoronoiLikeRegionGrowing< L, T >( m_srcImg, m_threshold.copy(), m_fillHoles );
	}

}
