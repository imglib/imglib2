package net.imglib2.ops.operation.labeling.unary;

import java.util.ArrayList;
import java.util.List;

import net.imglib2.Cursor;
import net.imglib2.labeling.Labeling;
import net.imglib2.labeling.LabelingType;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.ops.operation.randomaccessibleinterval.morph.unary.StructuringElementCursor;
import net.imglib2.view.Views;

/**
 * Erode operation on Labeling.
 * 
 * @author schoenenf, dietzc
 * 
 * @param <L>
 */
public class ErodeLabeling< L extends Comparable< L >> implements UnaryOperation< Labeling< L >, Labeling< L >>
{

	private final long[][] m_struc;

	private final boolean m_labelBased;

	public ErodeLabeling( final long[][] structuringElement )
	{
		this( structuringElement, true );
	}

	/**
	 * 
	 * @param structuringElement
	 * @param labelBased
	 *            Label-based / binary-based switch.
	 *            <ul>
	 *            <li>Label-based: Each region defined by a label is eroded
	 *            individually. If the label is not present in one of the
	 *            neighbor pixel it is also removed from the center.</li>
	 *            <li>Binary-based: The labeling is treated as a binary image.
	 *            If one of the neighbor pixels is empty the center pixel is
	 *            also set to the empty list.</li>
	 *            </ul>
	 */
	public ErodeLabeling( final long[][] structuringElement, final boolean labelBased )
	{
		m_struc = structuringElement;
		m_labelBased = labelBased;
	}

	@Override
	public Labeling< L > compute( final Labeling< L > input, final Labeling< L > output )
	{
		if ( m_labelBased )
		{
			return computeLabelBased( input, output );
		}
		else
		{
			return computeBinaryBased( input, output );
		}
	}

	private Labeling< L > computeLabelBased( final Labeling< L > input, final Labeling< L > output )
	{
		final StructuringElementCursor< LabelingType< L >> inStructure = new StructuringElementCursor< LabelingType< L >>( Views.extendValue( input, new LabelingType< L >() ).randomAccess(), m_struc );
		for ( final L label : input.getLabels() )
		{
			final Cursor< LabelingType< L >> out = input.getIterableRegionOfInterest( label ).getIterableIntervalOverROI( output ).localizingCursor();
			next: while ( out.hasNext() )
			{
				out.next();
				inStructure.relocate( out );
				while ( inStructure.hasNext() )
				{
					inStructure.next();
					if ( !inStructure.get().getLabeling().contains( label ) )
					{
						removeLabel( out.get(), label );
						continue next;
					}
				}
				addLabel( out.get(), label );
			}
		}
		return output;
	}

	private Labeling< L > computeBinaryBased( final Labeling< L > input, final Labeling< L > output )
	{
		final StructuringElementCursor< LabelingType< L >> inStructure = new StructuringElementCursor< LabelingType< L >>( Views.extendValue( input, new LabelingType< L >() ).randomAccess(), m_struc );
		final Cursor< LabelingType< L >> out = output.localizingCursor();
		next: while ( out.hasNext() )
		{
			out.next();
			inStructure.relocate( out );
			final List< L > center = inStructure.get().getLabeling();
			if ( center.isEmpty() )
			{
				out.get().setLabeling( out.get().getMapping().emptyList() );
				continue next;
			}
			while ( inStructure.hasNext() )
			{
				inStructure.next();
				if ( inStructure.get().getLabeling().isEmpty() )
				{
					out.get().setLabeling( out.get().getMapping().emptyList() );
					continue next;
				}
			}
			out.get().setLabeling( center );
		}
		return output;
	}

	private void addLabel( final LabelingType< L > type, final L elmnt )
	{
		if ( type.getLabeling().contains( elmnt ) ) { return; }
		final List< L > current = type.getLabeling();
		final ArrayList< L > tmp = new ArrayList< L >();
		tmp.addAll( current );
		tmp.add( elmnt );
		type.setLabeling( tmp );
	}

	private void removeLabel( final LabelingType< L > type, final L elmnt )
	{
		if ( !type.getLabeling().contains( elmnt ) ) { return; }
		final List< L > current = type.getLabeling();
		final ArrayList< L > tmp = new ArrayList< L >();
		tmp.addAll( current );
		tmp.remove( elmnt );
		type.setLabeling( tmp );
	}

	@Override
	public UnaryOperation< Labeling< L >, Labeling< L >> copy()
	{
		return new ErodeLabeling< L >( m_struc, m_labelBased );
	}
}
