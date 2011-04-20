/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * @author Lee Kamentsky
 *
 */
package mpicbg.imglib.labeling;

import java.util.Collection;

import mpicbg.imglib.roi.IterableRegionOfInterest;
import mpicbg.imglib.roi.RegionOfInterest;

/**
 * A labeling cursor strategy provides label cursors, bounds and other
 * potentially cacheable parameters on the labeling. It may be advantageous
 * to find all pixels for each label only once for sparse labelings,
 * small spaces and labelings that do not change during cursor use.
 * 
 * @author leek
 *
 * @param <T>
 * @param <L>
 */
/**
 * @author leek
 *
 * @param <T>
 * @param <L>
 */
public interface LabelingROIStrategy<T extends Comparable<T>, L extends Labeling<T>> {
	/**
	 * A region of interest designed to be queried for point-membership
	 * 
	 * @param label - the label of the pixels to be traversed
	 * @return a region of interest which is a RandomAccessible with a boolean
	 *         "yes I am a member / no I am not a member" value.
	 */
	public RegionOfInterest createRegionOfInterest(T label);

	/**
	 * An iterable region of interest, optimized for iteration
	 * over the pixels in the region of interest.
	 *  
	 * @param label - the label to be traversed
	 * @return a cursor over the perimeter of the labeled object
	 */
	public IterableRegionOfInterest createIterableRegionOfInterest(T label);
	
	/**
	 * Get the extents of the bounding box around the labeled object
	 * @param label - the label of the object in question
	 * @param minExtents - on input, must be an array of size D or larger
	 * where D is the dimension of the space. On output, the minimum value 
	 * of the pixel coordinates for the labeled object is stored in the array.
	 * @param maxExtents - the maximum value of the pixel coordinates for
	 * the labeled object.
	 * @return true if some pixel has the label
	 */
	public boolean getExtents(T label, long [] minExtents, long [] maxExtents);
	
	/**
	 * The coordinates of the first pixel with the given label when raster
	 * scanning through the space.
	 * 
	 * There is a class of algorithms on labeled objects that trace around
	 * the perimeter or surface of the object, starting with the first
	 * pixel encountered in a raster scan (given a space S(x[1],..x[D])
	 * of dimension D, sort the coordinates of the labeled pixels by increasing
	 * x[1], then increasing x[2], etc and pick the first in the list). This
	 * function can be used to find the pixel.
	 * 
	 * Note: the algorithms typically assume that all similarly labeled pixels are
	 * connected and that either there are no holes or holes are not
	 * significant.
	 * 
	 * @param label - search on pixels of this label
	 * @param start - on input, an array of at least size D, on output
	 * the coordinates of the raster start.
	 * @return true if some pixel has the label
	 */
	public boolean getRasterStart(T label, long []start);
	/**
	 * Return the area of the labeled object in units of pixels or the volume
	 * of the labeled object in voxels (or the hyper-volume of the 4/5-d
	 * object in ?toxels? or ?spectracels? or ?spectratoxels?)
	 * @return the area of the labeled object in pixels.
	 */
	public long getArea(T label);
	/**
	 * Find all of the labels used to label pixels.
	 * @return array of labels used.
	 */
	public Collection<T> getLabels();
}
