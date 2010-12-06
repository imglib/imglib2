package mpicbg.imglib.labeling;

import java.util.Arrays;

/**
 * The bounding box that contains a region from the minimum inclusive to
 * the maximum non-inclusive.
 * 
 * @author leek
 *
 */
public class BoundingBox {
	protected int [] min;
	protected int [] max;
	public BoundingBox(int dimensions) {
		min = new int [dimensions];
		max = new int [dimensions];
		Arrays.fill(max, Integer.MIN_VALUE);
		Arrays.fill(min, Integer.MAX_VALUE);
	}
	
	/**
	 * @return the # of dimensions in the bounding box's space (e.g. 2, 3)
	 */
	public int getDimensions() {
		return min.length;
	}
	/**
	 * the minimum and maximum extents of the box
	 * @param destMin - on input, an array of at least size D, the dimension of
	 * the space. On output, the minimum extents of the bounding box.
	 * @param destMax - on output, the maximum extents of the bounding box.
	 */
	public void getExtents(int [] destMin, int [] destMax) {
		if (destMin != null)
			System.arraycopy(min, 0, destMin, 0, min.length);
		if (destMax != null)
			System.arraycopy(max, 0, destMax, 0, max.length);
	}

	/**
	 * update the minimum and maximum extents with the given coordinates.
	 * @param coordinates
	 */
	public void update(final int [] coordinates) {
		for (int i = 0; i<min.length; i++) {
			if (coordinates[i] < min[i]) min[i] = coordinates[i];
			if (coordinates[i] >= max[i]) max[i] = coordinates[i]+1;
		}
	}
}
