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

import net.imglib2.roi.IterableRegionOfInterest;
import net.imglib2.roi.RegionOfInterest;

/**
 * A labeling cursor strategy provides label cursors, bounds and other
 * potentially cacheable parameters on the labeling. It may be advantageous to
 * find all pixels for each label only once for sparse labelings, small spaces
 * and labelings that do not change during cursor use.
 * 
 * @param <T>
 * @param <L>
 * 
 * @author Lee Kamentsky
 */
public interface LabelingROIStrategy< T extends Comparable< T >, L extends Labeling< T >>
{
	/**
	 * A region of interest designed to be queried for point-membership
	 * 
	 * @param label
	 *            - the label of the pixels to be traversed
	 * @return a region of interest which is a RandomAccessible with a boolean
	 *         "yes I am a member / no I am not a member" value.
	 */
	public RegionOfInterest createRegionOfInterest( T label );

	/**
	 * An iterable region of interest, optimized for iteration over the pixels
	 * in the region of interest.
	 * 
	 * @param label
	 *            - the label to be traversed
	 * @return a cursor over the perimeter of the labeled object
	 */
	public IterableRegionOfInterest createIterableRegionOfInterest( T label );

	/**
	 * Get the extents of the bounding box around the labeled object
	 * 
	 * @param label
	 *            - the label of the object in question
	 * @param minExtents
	 *            - on input, must be an array of size D or larger where D is
	 *            the dimension of the space. On output, the minimum value of
	 *            the pixel coordinates for the labeled object is stored in the
	 *            array.
	 * @param maxExtents
	 *            - the maximum value of the pixel coordinates for the labeled
	 *            object.
	 * @return true if some pixel has the label
	 */
	public boolean getExtents( T label, long[] minExtents, long[] maxExtents );

	/**
	 * The coordinates of the first pixel with the given label when raster
	 * scanning through the space.
	 * 
	 * There is a class of algorithms on labeled objects that trace around the
	 * perimeter or surface of the object, starting with the first pixel
	 * encountered in a raster scan (given a space S(x[1],..x[D]) of dimension
	 * D, sort the coordinates of the labeled pixels by increasing x[1], then
	 * increasing x[2], etc and pick the first in the list). This function can
	 * be used to find the pixel.
	 * 
	 * Note: the algorithms typically assume that all similarly labeled pixels
	 * are connected and that either there are no holes or holes are not
	 * significant.
	 * 
	 * @param label
	 *            - search on pixels of this label
	 * @param start
	 *            - on input, an array of at least size D, on output the
	 *            coordinates of the raster start.
	 * @return true if some pixel has the label
	 */
	public boolean getRasterStart( T label, long[] start );

	/**
	 * Return the area of the labeled object in units of pixels or the volume of
	 * the labeled object in voxels (or the hyper-volume of the 4/5-d object in
	 * ?toxels? or ?spectracels? or ?spectratoxels?)
	 * 
	 * @return the area of the labeled object in pixels.
	 */
	public long getArea( T label );

	/**
	 * Find all of the labels used to label pixels.
	 * 
	 * @return array of labels used.
	 */
	public Collection< T > getLabels();
}
