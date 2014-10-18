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

import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.roi.IterableRegionOfInterest;
import net.imglib2.roi.RegionOfInterest;

/**
 * A labeling provides methods to get at ROIs representing each of the labeled
 * objects in addition to image-like methods to discover the labels at given
 * pixels.
 * 
 * @author Lee Kamentsky
 */
public interface Labeling< T extends Comparable< T >> extends RandomAccessibleInterval< LabelingType< T >>, IterableInterval< LabelingType< T >>
{
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
	public boolean getExtents( T label, long[] minExtents, long[] maxExtents );

	/**
	 * Find the first pixel in a raster scan of the object with the given label.
	 */
	public boolean getRasterStart( T label, long[] start );

	/**
	 * Return the area or suitable N-d analog of the labeled object
	 * 
	 * @param label
	 *            - label for object in question
	 * @return area in units of pixel / voxel / etc.
	 */
	public long getArea( T label );

	/**
	 * Find all labels in the space
	 * 
	 * @return a collection of the labels.
	 */
	public Collection< T > getLabels();

	/**
	 * Get a region of interest optimized to determine point membership
	 * 
	 * @param label
	 *            The ROI will represent the area labeled with this label
	 * @return a region of interest
	 */
	public RegionOfInterest getRegionOfInterest( T label );

	/**
	 * Get a ROI that represents the pixels with the given label
	 */
	public IterableRegionOfInterest getIterableRegionOfInterest( T label );

	/**
	 * Copy method
	 * 
	 * @return copy of the labeling
	 */
	public Labeling< T > copy();

	/**
	 * Factory
	 * 
	 * @return create new labeling
	 */
	public < LL extends Comparable< LL >> LabelingFactory< LL > factory();

}
