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

package net.imglib2.labeling;

import java.util.Collection;

import net.imglib2.AbstractInterval;
import net.imglib2.img.AbstractImg;
import net.imglib2.roi.IterableRegionOfInterest;
import net.imglib2.roi.RegionOfInterest;

/**
 * A labeling represents the assignment of zero or more labels to the pixels in
 * a space.
 * 
 * @param <T>
 *            - the type used to label the pixels, for instance string names for
 *            user-assigned object labels or integers for machine-labeled
 *            images.
 * 
 * @author Lee Kamentsky
 */
public abstract class AbstractLabeling< T extends Comparable< T >> extends AbstractInterval implements Labeling< T >
{

	protected LabelingROIStrategy< T, ? extends Labeling< T >> strategy;

	protected long size;

	protected AbstractLabeling( final long[] size, final LabelingROIStrategyFactory< T > factory )
	{
		super( size );
		this.size = AbstractImg.numElements( size );
		this.strategy = factory.createLabelingROIStrategy( this );
	}

	@Override
	public long size()
	{
		return size;
	}

	/**
	 * Use an alternative strategy for making labeling cursors.
	 * 
	 * @param strategy
	 *            - a strategy for making labeling cursors.
	 */
	public void setLabelingCursorStrategy( final LabelingROIStrategy< T, ? extends Labeling< T >> strategy )
	{
		this.strategy = strategy;
	}

	@Override
	public RegionOfInterest getRegionOfInterest( final T label )
	{
		return strategy.createRegionOfInterest( label );
	}

	@Override
	public IterableRegionOfInterest getIterableRegionOfInterest( final T label )
	{
		return strategy.createIterableRegionOfInterest( label );
	}

	/**
	 * find the coordinates of the bounding box around the given label. The the
	 * minimum extents are inclusive (there will be pixels at the coordinates of
	 * the minimum extents) and the maximum extents are exclusive(all pixels
	 * will have coordinates less than the maximum extents)
	 * 
	 * @param label
	 *            - find pixels with this label
	 * @return true if some pixels are labeled, false if none have the label
	 */
	@Override
	public boolean getExtents( final T label, final long[] minExtents, final long[] maxExtents )
	{
		return strategy.getExtents( label, minExtents, maxExtents );
	}

	/**
	 * Find the first pixel in a raster scan of the object with the given label.
	 */
	@Override
	public boolean getRasterStart( final T label, final long[] start )
	{
		return strategy.getRasterStart( label, start );
	}

	/**
	 * Return the area or suitable N-d analog of the labeled object
	 * 
	 * @param label
	 *            - label for object in question
	 * @return area in units of pixel / voxel / etc.
	 */
	@Override
	public long getArea( final T label )
	{
		return strategy.getArea( label );
	}

	/**
	 * Find all labels in the space
	 * 
	 * @return a collection of the labels.
	 */
	@Override
	public Collection< T > getLabels()
	{
		return strategy.getLabels();
	}

}
