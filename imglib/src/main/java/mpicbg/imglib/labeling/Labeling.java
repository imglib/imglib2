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

import mpicbg.imglib.container.Container;
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.type.label.FakeType;

/**
 * A labeling represents the assignment of zero or more labels to the
 * pixels in a space.
 * 
 * @author Lee Kamentsky
 *
 * @param <T> - the type used to label the pixels, for instance string
 * names for user-assigned object labels or integers for machine-labeled
 * images.
 */
public class Labeling<T extends Comparable<T>> extends Image<LabelingType<T>> {

	protected LabelingCursorStrategy<T, Labeling<T>> strategy;
	/**
	 * Create a named labeling from a container and a type
	 * @param container - container appropriate for storing the type
	 * @param type - an instance of the labeling's type for generic resolution
	 * @param name - the name to assign
	 */
	public Labeling(Container<LabelingType<T>> container, LabelingType<T> type,
			String name) {
		super(container, type, name);
		strategy = new DefaultLabelingCursorStrategy<T, Labeling<T>>(this);
	}

	/**
	 * Create a named labeling from an image factory
	 * @param imageFactory
	 * @param dim - dimensions of the image
	 * @param name - image name
	 */
	public Labeling(ImageFactory<LabelingType<T>> imageFactory, int[] dim,
			String name) {
		super(imageFactory, dim, name);
		strategy = new DefaultLabelingCursorStrategy<T, Labeling<T>>(this);
	}

	public Labeling(Container<LabelingType<T>> container, LabelingType<T> type) {
		super(container, type);
		strategy = new DefaultLabelingCursorStrategy<T, Labeling<T>>(this);
	}
	
	/**
	 * Use an alternative strategy for making labeling cursors.
	 * @param strategy - a strategy for making labeling cursors.
	 */
	public void setLabelingCursorStrategy(LabelingCursorStrategy<T, Labeling<T>> strategy) {
		this.strategy = strategy;
	}
	
	/* (non-Javadoc)
	 * @see mpicbg.imglib.image.Image#createNewImage(int[], java.lang.String)
	 */
	@Override
	public Image<LabelingType<T>> createNewImage(int[] dimensions, String name) {
		return new Labeling<T>(this.getImageFactory(),dimensions, name);
	}

	/* (non-Javadoc)
	 * @see mpicbg.imglib.image.Image#createNewImage(int[])
	 */
	@Override
	public Image<LabelingType<T>> createNewImage(int[] dimensions) {
		return createNewImage(dimensions, null);
	}

	/* (non-Javadoc)
	 * @see mpicbg.imglib.image.Image#createNewImage(java.lang.String)
	 */
	@Override
	public Image<LabelingType<T>> createNewImage(String name) {
		return createNewImage(getDimensions(), name);
	}

	/* (non-Javadoc)
	 * @see mpicbg.imglib.image.Image#createNewImage()
	 */
	@Override
	public Image<LabelingType<T>> createNewImage() {
		return createNewImage(getDimensions(), null);
	}

	/**
	 * Return a new named labeling using this labeling's factory 
	 * @param dimensions - dimensions of the new labeling
	 * @param name - it's name
	 * @return a new labeling
	 */
	public Labeling<T> createNewLabeling(int[] dimensions, String name) {
		return new Labeling<T>(this.getImageFactory(),dimensions, name);
	}

	/**
	 * Create a new, unnamed labeling
	 * @param dimensions - dimensions of the labeling's space
	 * @return a new labeling created using this labeling's factory
	 */
	public Labeling<T> createNewLabeling(int[] dimensions) {
		return createNewLabeling(dimensions, null);
	}

	/**
	 * Create a new named labeling whose dimensions are the same as
	 * the current labeling
	 * @param name name for the new labeling
	 * @return a newly created labeling
	 */
	public Labeling<T> createNewLabeling(String name) {
		return createNewLabeling(getDimensions(), name);
	}

	/**
	 * Create a new unnamed labeling of the same dimensions as this one
	 * @return a newly created labeling
	 */
	public Labeling<T> createNewLabeling() {
		return createNewLabeling(getDimensions(), null);
	}
	/**
	 * Create a localizable cursor that can be used to find the pixels
	 * associated with a particular label.
	 * @param label - the label to find
	 * @return
	 */
	public LocalizableCursor<FakeType> createLocalizableLabelCursor(T label) {
		return strategy.createLocalizableLabelCursor(label);
	}
	
	/**
	 * Create a localizable cursor that can be used to find the pixels
	 * in the perimeter of a label.
	 * @param label
	 * @return
	 */
	public LocalizableCursor<FakeType> createLocalizablePerimeterCursor(T label) {
		return strategy.createLocalizablePerimeterCursor(label);
	}
	
	/**
	 * find the coordinates of the bounding box around the given 
	 * label. The the minimum extents are inclusive (there will be pixels
	 * at the coordinates of the minimum extents) and the maximum
	 * extents are exclusive(all pixels will have coordinates less than
	 * the maximum extents)
	 * @param label - find pixels with this label
	 * @return true if some pixels are labeled, false if none have the label
	 */
	public boolean getExtents(T label, int [] minExtents, int [] maxExtents) {
		return strategy.getExtents(label, minExtents, maxExtents);
	}
	
	/**
	 * Find the first pixel in a raster scan of the object with the given label.
	 * 
	 * @param label
	 * @param start
	 * @return
	 */
	public boolean getRasterStart(T label, int [] start) {
		return strategy.getRasterStart(label, start);
	}
	
	/**
	 * Return the area or suitable N-d analog of the labeled object
	 * @param label - label for object in question
	 * @return area in units of pixel / voxel / etc.
	 */
	public long getArea(T label) {
		return strategy.getArea(label);
	}
	
	/**
	 * Find all labels in the space
	 * @return a collection of the labels.
	 */
	public Collection<T> getLabels() {
		return strategy.getLabels();
	}
}
