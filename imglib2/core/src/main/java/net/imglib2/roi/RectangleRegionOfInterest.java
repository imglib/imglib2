package net.imglib2.roi;

import net.imglib2.RealLocalizable;

/**
 * @author leek
 *
 * An N-dimensional (hyper) rectangle whose edges are
 * orthogonal to the coordinate system.
 * 
 * The rectangle is defined by:
 * <ul><li>an origin which is the vertex at the minima
 * of the rectangle's extent in the space.</li>
 * <li>an extent which is the dimension of the region of interest
 * extending from the origin</li></ul>
 * 
 */
public class RectangleRegionOfInterest extends AbstractIterableRegionOfInterest {

	private final double [] origin;
	private final double [] extent;
	public RectangleRegionOfInterest(double [] origin, double [] extent ) {
		super(origin.length);
		this.origin = origin;
		this.extent = extent;
	}
	
	/**
	 * Set the origin using a point. Updating the origin will move the rectangle
	 * without changing its size.
	 * 
	 * @param ptOrigin - new origin. This should define the minima of the rectangle
	 */
	public void setOrigin(RealLocalizable ptOrigin) {
		ptOrigin.localize(origin);
		this.invalidateCachedState();
	}
	
	/**
	 * Set the origin using a double array of coordinates. Updating the origin will move the rectangle
	 * without changing its size.
	 * @param origin the coordinates of the minima of the rectangle
	 */
	public void setOrigin(double [] origin) {
		System.arraycopy(origin, 0, this.origin, 0, numDimensions());
		invalidateCachedState();
	}
	
	/**
	 * Set the origin for a particular coordinate
	 * @param origin new value of the minimum of the rectangle at the given coordinate
	 * @param d zero-based index of the dimension to be affected
	 */
	public void setOrigin(double origin, int d) {
		this.origin[d] = origin;
		invalidateCachedState();
	}
	
	/**
	 * Set the extent of the rectangle. Setting the extent will change
	 * the rectangle's size while maintaining the position of the origin.
	 * 
	 * @param extent the extent (width, height, depth, duration, etc) of the rectangle
	 */
	public void setExtent(double [] extent) {
		System.arraycopy(extent, 0, this.extent, 0, numDimensions());
		invalidateCachedState();
	}

	/**
	 * Set the extent for a single dimension
	 * @param extent
	 * @param d
	 */
	public void setExtent(double extent, int d) {
		this.extent[d] = extent;
		invalidateCachedState();
	}
	
	/**
	 * Copy the extents of the rectangle into the array provided
	 * @param extent on output, the extent of the rectanble
	 */
	public void getExtent(double [] extent) {
		System.arraycopy(this.extent, 0, extent, 0, numDimensions());
	}
	
	/**
	 * Get the extent of the rectangle in one dimension
	 * @param d dimension in question
	 * @return extent (eg. width, height) of rectangle in given dimension
	 */
	public double getExtent(int d) {
		return extent[d];
	}
	
	@Override
	protected boolean nextRaster(long[] position, long[] end) {
		/*
		 * Check for before
		 */
		for (int i=numDimensions()-1; i >= 0; i--) {
			if (position[i] < min(i)){
				for (; i >= 0; i--) {
					position[i] = end[i] = min(i);
				}
				end[0] = max(0)+1;
				return true;
			}
		}
		position[0] = min(0);
		end[0] = max(0) + 1;
		for (int i=1; i<numDimensions(); i++) {
			position[i] = end[i] = position[i] + 1;
			if (position[i] <= max(i)) return true;
			position[i] = end[i] = min(i);
		}
		return false;
	}

	@Override 
	protected boolean isMember(double[] position) {
		for (int i=0; i<numDimensions(); i++) {
			if (position[i] < realMin(i)) return false;
			if (position[i] >= realMax(i)) return false;
		}
		return true;
	}

	@Override
	protected long size() {
		long product = 1;
		for (int i=0; i<numDimensions(); i++) {
			product *= max(i) - min(i) + 1;
		}
		return product;
	}

	@Override
	protected void getExtrema(long[] minima, long[] maxima) {
		for (int i=0; i<numDimensions(); i++) {
			minima[i] = (long)Math.ceil(origin[i]);
			maxima[i] = (long)Math.ceil(origin[i] + extent[i])-1;
		}
	}

	@Override
	protected void getRealExtrema(double[] minima, double[] maxima) {
		System.arraycopy(origin, 0, minima, 0, numDimensions());
		for (int i=0; i < numDimensions(); i++) {
			maxima[i] = origin[i] + extent[i];
		}
	}

}
