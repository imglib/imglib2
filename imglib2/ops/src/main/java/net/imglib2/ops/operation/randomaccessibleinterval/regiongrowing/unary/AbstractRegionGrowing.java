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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.labeling.LabelingType;
import net.imglib2.labeling.NativeImgLabeling;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.type.Type;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.Pair;

/**
 * TODO: Efficiency!!!!
 * 
 * @author hornm, dietzc, schoenenf, University of Konstanz
 */
public abstract class AbstractRegionGrowing< T extends Type< T >, L extends Comparable< L >, I extends IterableInterval< T > & RandomAccessibleInterval< T >, LL extends RandomAccessibleInterval< LabelingType< L >> & IterableInterval< LabelingType< L >>> implements UnaryOperation< I, LL >
{

	private final GrowingMode m_mode;

	private final Map< L, List< L >> m_labelMap;

	private final boolean m_allowOverlap;

	private RandomAccess< BitType > m_visitedRA = null;

	private RandomAccess< LabelingType< L >> m_visitedLabRA = null;

	protected final long[][] m_structuringElement;

	/**
	 * 
	 * @author hornm, University of Konstanz
	 */
	public enum GrowingMode
	{
		/**
		 * In synchronous mode, the seeding points are grown after each other
		 */
		SYNCHRONOUS,

		/**
		 * in asynchronous mode, first all seeding points are add to the queue
		 * and then the growing process is started
		 */
		ASYNCHRONOUS;
	}

	/**
	 * @param structuringElement
	 *            set of offsets defining the neighbourhood
	 * @param mode
	 * @param allowOverlap
	 *            allows overlapping, more memory intensive
	 */
	public AbstractRegionGrowing( long[][] structuringElement, GrowingMode mode, boolean allowOverlap )
	{

		m_structuringElement = structuringElement;
		m_mode = mode;
		m_allowOverlap = allowOverlap;
		m_labelMap = new HashMap< L, List< L >>();

	}

	private long[] resultDims( Interval src )
	{
		long[] dims = new long[ src.numDimensions() ];
		src.dimensions( dims );
		return dims;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LL compute( I op, LL r )
	{

		initRegionGrowing( op );

		final LinkedList< Pair< int[], L >> q = new LinkedList< Pair< int[], L >>();

		// image and random access to keep track of the already visited
		// pixel
		// positions
		if ( m_allowOverlap )
		{
			NativeImgLabeling< L, IntType > tmp = new NativeImgLabeling< L, IntType >( new ArrayImgFactory< IntType >().create( resultDims( op ), new IntType() ) );
			m_visitedLabRA = tmp.randomAccess();
		}
		else
		{
			BitType bt = new BitType();
			Img< BitType > tmp = null;
			try
			{
				tmp = new ArrayImgFactory< BitType >().imgFactory( bt ).create( op, bt );
			}
			catch ( IncompatibleTypeException e )
			{
				//
			}
			m_visitedRA = tmp.randomAccess();
		}

		// access to the resulting labeling
		RandomAccess< LabelingType< L >> resRA = r.randomAccess();

		L label;
		int[] pos = new int[ op.numDimensions() ];
		do
		{
			while ( ( label = nextSeedPosition( pos ) ) != null )
			{

				// already visited?
				setVisitedPosition( pos );
				if ( isMarkedAsVisited( label ) )
				{
					continue;
				}
				markAsVisited( label );

				q.addLast( new Pair< int[], L >( pos.clone(), label ) );

				// set new labeling
				resRA.setPosition( pos );
				setLabel( resRA, label );

				if ( m_mode == GrowingMode.ASYNCHRONOUS )
				{
					growProcess( q, resRA, op );
				}
			}
			if ( m_mode == GrowingMode.SYNCHRONOUS )
			{
				growProcess( q, resRA, op );
			}
		}
		while ( hasMoreSeedingPoints() );

		return r;
	}

	/*
	 * The actual growing process. Grows a region by iterativevly calling the
	 * includeInRegion method till the queue is empty.
	 */
	private synchronized void growProcess( LinkedList< Pair< int[], L >> q, RandomAccess< LabelingType< L >> resLabRA, I src )
	{
		int[] pos, nextPos;
		L label;
		boolean outOfBounds;
		while ( !q.isEmpty() )
		{
			Pair< int[], L > p = q.removeFirst();
			pos = p.a;
			label = p.b;

			// if (resRA.get().getLabeling() == label) {
			// continue;
			// }

			for ( long[] offset : m_structuringElement )
			{
				outOfBounds = false;
				nextPos = pos.clone();
				for ( int i = 0; i < pos.length; i++ )
				{
					nextPos[ i ] = pos[ i ] + ( int ) offset[ i ];
					if ( nextPos[ i ] < 0 || nextPos[ i ] >= src.dimension( i ) )
					{
						outOfBounds = true;
						break;
					}
				}
				if ( !outOfBounds )
				{
					updatePosition( resLabRA, q, pos, nextPos, label );
				}

			}

		}
		queueProcessed();

	}

	/*
	 * Updates a position, i.e. if not visited yet, it marks it as visited, sets
	 * the according label and adds the position to the queue
	 */
	private void updatePosition( RandomAccess< LabelingType< L >> resLabRA, LinkedList< Pair< int[], L >> queue, int[] pos, int[] nextPos, L label )
	{
		setVisitedPosition( nextPos );
		// if already visited, return
		if ( isMarkedAsVisited( label ) ) { return; }

		if ( !includeInRegion( pos, nextPos, label ) ) { return; }

		// mark position as processed
		markAsVisited( label );

		queue.addLast( new Pair< int[], L >( nextPos, label ) );

		// update the ra's positions
		resLabRA.setPosition( nextPos );
		setLabel( resLabRA, label );
	}

	/**
	 * Sets the label in the result labeling. To speed up it a bit, a map is
	 * used to get the already interned list of single labels.
	 */
	protected void setLabel( RandomAccess< LabelingType< L >> ra, L label )
	{
		List< L > labeling;
		if ( ra.get().getLabeling().isEmpty() )
		{
			if ( ( labeling = m_labelMap.get( label ) ) == null )
			{
				// add the label and put the interned list into
				// the hash map
				labeling = new ArrayList< L >( 1 );
				labeling.add( label );
				labeling = ra.get().getMapping().intern( labeling );
			}
		}
		else
		{
			labeling = new ArrayList< L >( ra.get().getLabeling() );
			labeling.add( label );

		}
		ra.get().setLabeling( labeling );

	}

	private void setVisitedPosition( int[] pos )
	{
		if ( m_allowOverlap )
		{
			m_visitedLabRA.setPosition( pos );
		}
		else
		{
			m_visitedRA.setPosition( pos );
		}
	}

	/*
	 * Marks the set position as visited. To keep this, either a bittype image
	 * or a labeling is used (depending if overlap is allowed or not).
	 */
	private boolean isMarkedAsVisited( L label )
	{
		if ( m_allowOverlap )
		{
			return m_visitedLabRA.get().getLabeling().contains( label );
		}
		else
		{
			return m_visitedRA.get().get();
		}
	}

	/*
	 * Checks if a postion was already visited. To keep this, either a bittype
	 * image or a labeling is used (depending if overlap is allowed or not).
	 */
	private void markAsVisited( L label )
	{
		if ( m_allowOverlap )
		{
			List< L > l = new ArrayList< L >( m_visitedLabRA.get().getLabeling() );
			l.add( label );
			m_visitedLabRA.get().setLabeling( l );
		}
		else
		{
			m_visitedRA.get().set( true );
		}
	}

	/**
	 * Called before the growing process is started.
	 * 
	 * @param srcImg
	 */
	protected abstract void initRegionGrowing( I srcImg );

	/**
	 * 
	 * 
	 * @param seedPos
	 * @return the next seeding point, {@code null} if no more seeding points
	 *         are available
	 */
	protected abstract L nextSeedPosition( int[] seedPos );

	/**
	 * @param oldPos
	 *            the position, whose neighbour {@code nextPos} is
	 * @param nextPos
	 * @param label
	 * @return true, if the new position ({@code nextPos}) should get the given
	 *         label)
	 */
	protected abstract boolean includeInRegion( int[] oldPos, int[] nextPos, L label );

	/**
	 * Called if one grow step was finished, i.e. the position queue run empty.
	 */
	protected abstract void queueProcessed();

	/**
	 * @return
	 */
	protected boolean hasMoreSeedingPoints()
	{
		return false;
	}

	/**
	 * Return an array of offsets to the 8-connected (or N-d equivalent)
	 * structuring element for the dimension space. The structuring element is
	 * the list of offsets from the center to the pixels to be examined.
	 * 
	 * @param dimensions
	 * @return the structuring element.
	 */
	public static long[][] get8ConStructuringElement( int dimensions )
	{
		int nElements = 1;
		for ( int i = 0; i < dimensions; i++ )
			nElements *= 3;
		nElements--;
		long[][] result = new long[ nElements ][ dimensions ];
		long[] position = new long[ dimensions ];
		Arrays.fill( position, -1 );
		for ( int i = 0; i < nElements; i++ )
		{
			System.arraycopy( position, 0, result[ i ], 0, dimensions );
			/*
			 * Special case - skip the center element.
			 */
			if ( i == nElements / 2 - 1 )
			{
				position[ 0 ] += 2;
			}
			else
			{
				for ( int j = 0; j < dimensions; j++ )
				{
					if ( position[ j ] == 1 )
					{
						position[ j ] = -1;
					}
					else
					{
						position[ j ]++;
						break;
					}
				}
			}
		}
		return result;
	}

	/**
	 * Return an array of offsets to the -connected (or N-d equivalent)
	 * structuring element for the dimension space. The structuring element is
	 * the list of offsets from the center to the pixels to be examined.
	 * 
	 * @param dimensions
	 * @return the structuring element.
	 */
	public static long[][] get4ConStructuringElement( int dimensions )
	{
		int nElements = dimensions * 2;

		long[][] result = new long[ nElements ][ dimensions ];
		for ( int d = 0; d < dimensions; d++ )
		{
			result[ d * 2 ] = new long[ dimensions ];
			result[ d * 2 + 1 ] = new long[ dimensions ];
			result[ d * 2 ][ d ] = -1;
			result[ d * 2 + 1 ][ d ] = 1;

		}
		return result;
	}
}
