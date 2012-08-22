package net.imglib2.ops.operation.labeling.unary;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.imglib2.Cursor;
import net.imglib2.labeling.Labeling;
import net.imglib2.labeling.LabelingType;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.ops.operation.randomaccessibleinterval.regiongrowing.AbstractRegionGrowing;
import net.imglib2.util.Pair;

/**
 * 
 * @author dietzc, hornm University of Konstanz
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
