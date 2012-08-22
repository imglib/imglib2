package net.imglib2.ops.operation.randomaccessibleinterval.regiongrowing.unary;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.labeling.LabelingType;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.type.NativeType;

/**
 * nD Connected Component Analysis.
 * 
 * @author hornm, dietzc University of Konstanz
 */
public class CCA< T extends NativeType< T > & Comparable< T >, I extends RandomAccessibleInterval< T > & IterableInterval< T >, LL extends RandomAccessibleInterval< LabelingType< Integer >> & IterableInterval< LabelingType< Integer >>> extends AbstractRegionGrowing< T, Integer, I, LL >
{

	private Cursor< T > srcCur;

	private RandomAccess< T > srcRA;

	private Integer m_labelNumber;

	private final T m_background;

	private ThreadSafeLabelNumbers m_synchronizer;

	/**
	 * @param structuringElement
	 * @param background
	 */
	public CCA( long[][] structuringElement, T background )
	{
		this( structuringElement, background, null );
	}

	/**
	 * @param structuringElement
	 * @param background
	 */
	public CCA( long[][] structuringElement, T background, ThreadSafeLabelNumbers synchronizer )
	{
		super( structuringElement, GrowingMode.ASYNCHRONOUS, false );

		if ( synchronizer == null )
			m_synchronizer = new ThreadSafeLabelNumbers();
		else
			m_synchronizer = synchronizer;

		m_background = background;
		m_labelNumber = m_synchronizer.aquireNewLabelNumber();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initRegionGrowing( I srcImg )
	{
		srcCur = srcImg.localizingCursor();
		srcRA = srcImg.randomAccess();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Integer nextSeedPosition( int[] seedPos )
	{
		while ( srcCur.hasNext() )
		{
			srcCur.fwd();
			if ( srcCur.get().compareTo( m_background ) != 0 )
			{
				srcCur.localize( seedPos );
				return m_labelNumber;
			}
		}
		return null;

	}

	protected synchronized Integer labelNumber()
	{
		return m_labelNumber;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean includeInRegion( int[] oldPos, int[] nextPos, Integer label )
	{
		srcRA.setPosition( nextPos );
		return srcRA.get().compareTo( m_background ) != 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected synchronized void queueProcessed()
	{
		m_labelNumber = m_synchronizer.aquireNewLabelNumber();

	}

	@Override
	public UnaryOperation< I, LL > copy()
	{
		return new CCA< T, I, LL >( m_structuringElement.clone(), m_background.copy(), m_synchronizer );
	}
}
