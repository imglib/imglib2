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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.labeling.Labeling;
import net.imglib2.labeling.LabelingType;
import net.imglib2.labeling.NativeImgLabeling;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.type.Type;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.ValuePair;

/**
 * TODO: Efficiency!!!!
 *
 * @author Martin Horn (University of Konstanz)
 */
public abstract class AbstractRegionGrowing< T extends Type< T >, L extends Comparable< L >> implements UnaryOperation< RandomAccessibleInterval< T >, Labeling< L >>
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
    public AbstractRegionGrowing( final long[][] structuringElement, final GrowingMode mode, final boolean allowOverlap )
    {

        m_structuringElement = structuringElement;
        m_mode = mode;
        m_allowOverlap = allowOverlap;
        m_labelMap = new HashMap< L, List< L >>();

    }

    private long[] resultDims( final Interval src )
    {
        long[] dims = new long[ src.numDimensions() ];
        src.dimensions( dims );
        return dims;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Labeling< L > compute( final RandomAccessibleInterval< T > op, final Labeling< L > r )
    {

        initRegionGrowing( op );

        final LinkedList< ValuePair< int[], L >> q = new LinkedList< ValuePair< int[], L >>();

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

                q.addLast( new ValuePair< int[], L >( pos.clone(), label ) );

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
    private synchronized void growProcess( final LinkedList< ValuePair< int[], L >> q, final RandomAccess< LabelingType< L >> resLabRA, final RandomAccessibleInterval< T > src )
    {
        int[] pos, nextPos;
        L label;
        boolean outOfBounds;
        while ( !q.isEmpty() )
        {
            ValuePair< int[], L > p = q.removeFirst();
            pos = p.a;
            label = p.b;

            // if (resRA.get().getLabeling() == label) {
            // continue;
            // }

            for ( long[] offset : m_structuringElement )
            {
                outOfBounds = false;
                nextPos = pos.clone();
                for ( int i = 0; i < pos.length; ++i )
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
    private void updatePosition( final RandomAccess< LabelingType< L >> resLabRA, final LinkedList< ValuePair< int[], L >> queue, final int[] pos, final int[] nextPos, final L label )
    {
        setVisitedPosition( nextPos );
        // if already visited, return
        if ( isMarkedAsVisited( label ) ) { return; }

        if ( !includeInRegion( pos, nextPos, label ) ) { return; }

        // mark position as processed
        markAsVisited( label );

        queue.addLast( new ValuePair< int[], L >( nextPos, label ) );

        // update the ra's positions
        resLabRA.setPosition( nextPos );
        setLabel( resLabRA, label );
    }

    /**
     * Sets the label in the result labeling. To speed up it a bit, a map is
     * used to get the already interned list of single labels.
     */
    protected void setLabel( final RandomAccess< LabelingType< L >> ra, final L label )
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

    private void setVisitedPosition( final int[] pos )
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
    private boolean isMarkedAsVisited( final L label )
    {
        if ( m_allowOverlap )
        {
            return m_visitedLabRA.get().getLabeling().contains( label );
        }
        return m_visitedRA.get().get();
    }

    /*
     * Checks if a postion was already visited. To keep this, either a bittype
     * image or a labeling is used (depending if overlap is allowed or not).
     */
    private void markAsVisited( final L label )
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
    protected abstract void initRegionGrowing( RandomAccessibleInterval< T > srcImg );

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
    public static long[][] get8ConStructuringElement( final int dimensions )
    {
        int nElements = 1;
        for ( int i = 0; i < dimensions; i++ ) {
            nElements *= 3;
        }
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
    public static long[][] get4ConStructuringElement( final int dimensions )
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
