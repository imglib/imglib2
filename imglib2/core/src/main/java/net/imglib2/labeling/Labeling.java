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
package net.imglib2.labeling;

import java.util.Collection;

import net.imglib2.img.Img;
import net.imglib2.roi.IterableRegionOfInterest;
import net.imglib2.roi.RegionOfInterest;

/**
 * @author leek
 *
 *         A labeling provides methods to get at ROIs representing each of the
 *         labeled objects in addition to image-like methods to discover the
 *         labels at given pixels.
 */
public interface Labeling< T extends Comparable< T >> extends Img< LabelingType< T >>
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
	 *
	 * @param label
	 * @param start
	 * @return
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
	 *
	 * @param label
	 * @return
	 */
	public IterableRegionOfInterest getIterableRegionOfInterest( T label );
}
