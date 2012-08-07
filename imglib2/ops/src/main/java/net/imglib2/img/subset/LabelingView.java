package net.imglib2.img.subset;

import java.util.Collection;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.labeling.DefaultROIStrategy;
import net.imglib2.labeling.Labeling;
import net.imglib2.labeling.LabelingFactory;
import net.imglib2.labeling.LabelingROIStrategy;
import net.imglib2.labeling.LabelingType;
import net.imglib2.roi.IterableRegionOfInterest;
import net.imglib2.roi.RegionOfInterest;
import net.imglib2.view.IterableRandomAccessibleInterval;
import net.imglib2.view.Views;

/**
 * Helper class to create a sub image.
 * 
 * @author dietzc, University of Konstanz
 */
public class LabelingView< L extends Comparable< L >> extends IterableRandomAccessibleInterval< LabelingType< L >> implements Labeling< L >
{

	protected LabelingROIStrategy< L, ? extends Labeling< L >> m_strategy;

	private final LabelingFactory< L > m_fac;

	private final IterableInterval< LabelingType< L >> m_ii;

	/**
	 * TODO: No metadata is saved here..
	 * 
	 * @see SubImg
	 * 
	 */
	public LabelingView( RandomAccessibleInterval< LabelingType< L >> in, LabelingFactory< L > fac )
	{
		super( in );
		m_fac = fac;
		m_strategy = new DefaultROIStrategy< L, Labeling< L >>( this );
		m_ii = Views.iterable( in );
	}

	@Override
	public boolean getExtents( L label, long[] minExtents, long[] maxExtents )
	{
		return m_strategy.getExtents( label, minExtents, maxExtents );
	}

	@Override
	public boolean getRasterStart( L label, long[] start )
	{
		return m_strategy.getRasterStart( label, start );
	}

	@Override
	public long getArea( L label )
	{
		return m_strategy.getArea( label );
	}

	@Override
	public Collection< L > getLabels()
	{
		return m_strategy.getLabels();
	}

	@Override
	public Cursor< LabelingType< L >> cursor()
	{
		return m_ii.cursor();
	}

	@Override
	public Cursor< LabelingType< L >> localizingCursor()
	{
		return m_ii.localizingCursor();
	}

	@Override
	public RegionOfInterest getRegionOfInterest( L label )
	{
		return m_strategy.createRegionOfInterest( label );
	}

	@Override
	public IterableRegionOfInterest getIterableRegionOfInterest( L label )
	{
		return m_strategy.createIterableRegionOfInterest( label );
	}

	@Override
	public Labeling< L > copy()
	{
		throw new UnsupportedOperationException( "TODO" );
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public < LL extends Comparable< LL >> LabelingFactory< LL > factory()
	{
		return ( LabelingFactory< LL > ) m_fac;
	}
}
