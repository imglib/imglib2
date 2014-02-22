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

package net.imglib2.ops.operation.subset.views;

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
 * Wrapper for and {@link RandomAccessibleInterval} of type {@link LabelingType}
 * 
 * @author Christian Dietz (University of Konstanz)
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
		m_ii = Views.flatIterable( in );
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
