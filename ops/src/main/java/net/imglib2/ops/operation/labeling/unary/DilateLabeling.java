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

package net.imglib2.ops.operation.labeling.unary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.imglib2.Cursor;
import net.imglib2.labeling.Labeling;
import net.imglib2.labeling.LabelingType;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.ops.operation.randomaccessibleinterval.unary.morph.StructuringElementCursor;
import net.imglib2.view.Views;

/**
 * Dilate operation on Labeling.
 * 
 * @author Christian Dietz (University of Konstanz)
 * @author Felix Schoenenberger (University of Konstanz)
 * 
 * @param <L>
 */
public class DilateLabeling< L extends Comparable< L >> implements UnaryOperation< Labeling< L >, Labeling< L >>
{

	private long[][] sortStructuringElement( final long[][] structuringElement )
	{
		final ArrayList< long[] > offsets = new ArrayList< long[] >();
		for ( final long[] off : structuringElement )
		{
			offsets.add( off );
		}
		Collections.sort( offsets, new Comparator< long[] >()
		{

			@Override
			public int compare( final long[] o1, final long[] o2 )
			{
				double dist1 = 0;
				double dist2 = 0;
				for ( int i = 0; i < o1.length; i++ )
				{
					dist1 += o1[ i ] * o1[ i ];
					dist2 += o2[ i ] * o2[ i ];
				}
				dist1 = Math.sqrt( dist1 );
				dist2 = Math.sqrt( dist2 );
				if ( dist1 < dist2 ) { return -1; }
				if ( dist1 > dist2 ) { return 1; }
				return 0;
			}
		} );
		final long[][] struc = new long[ offsets.size() ][];
		offsets.toArray( struc );
		return struc;
	}

	private final long[][] m_struc;

	private final boolean m_labelBased;

	public DilateLabeling( final long[][] structuringElement )
	{
		this( structuringElement, true );
	}

	/**
	 * 
	 * @param structuringElement
	 * @param labelBased
	 *            Label-based / binary-based switch.
	 *            <ul>
	 *            <li>Label-based: Each region defined by a label is dilated
	 *            individually. If the label is present in one of the neighbor
	 *            pixel it is also added to the center.</li>
	 *            <li>Binary-based: The labeling is treated as a binary image.
	 *            If one of the neighbor pixels is not empty the center pixel is
	 *            set to the nearest labeling. No new label combination is
	 *            composed.</li>
	 *            </ul>
	 */
	public DilateLabeling( final long[][] structuringElement, final boolean labelBased )
	{
		m_struc = labelBased ? structuringElement : sortStructuringElement( structuringElement );
		m_labelBased = labelBased;
	}

	@Override
	public Labeling< L > compute( final Labeling< L > input, final Labeling< L > output )
	{
		if ( m_labelBased ) { return computeLabelBased( input, output ); }
		return computeBinaryBased( input, output );
	}

	private Labeling< L > computeLabelBased( final Labeling< L > input, final Labeling< L > output )
	{
		final StructuringElementCursor< LabelingType< L >> outStructure = new StructuringElementCursor< LabelingType< L >>( Views.extendValue( output, new LabelingType< L >() ).randomAccess(), m_struc );
		for ( final L label : input.getLabels() )
		{
			final Cursor< LabelingType< L >> in = input.getIterableRegionOfInterest( label ).getIterableIntervalOverROI( input ).localizingCursor();
			while ( in.hasNext() )
			{
				in.next();
				outStructure.relocate( in );
				while ( outStructure.hasNext() )
				{
					outStructure.next();
					addLabel( outStructure.get(), label );
				}
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
			if ( !center.isEmpty() )
			{
				out.get().setLabeling( center );
				continue next;
			}
			while ( inStructure.hasNext() )
			{
				inStructure.next();
				if ( !inStructure.get().getLabeling().isEmpty() )
				{
					out.get().set( inStructure.get() );
					continue next;
				}
			}
			out.get().setLabeling( out.get().getMapping().emptyList() );
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

	@Override
	public UnaryOperation< Labeling< L >, Labeling< L >> copy()
	{
		return new DilateLabeling< L >( m_struc );
	}
}
