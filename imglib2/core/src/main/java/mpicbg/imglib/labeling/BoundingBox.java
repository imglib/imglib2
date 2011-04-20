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

import java.util.Arrays;

/**
 * The bounding box that contains a region from the minimum inclusive to
 * the maximum non-inclusive.
 * 
 * @author leek
 *
 */
public class BoundingBox {
	protected long [] min;
	protected long [] max;
	public BoundingBox(int dimensions) {
		min = new long [dimensions];
		max = new long [dimensions];
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
	public void getExtents(long [] destMin, long [] destMax) {
		if (destMin != null)
			System.arraycopy(min, 0, destMin, 0, min.length);
		if (destMax != null)
			System.arraycopy(max, 0, destMax, 0, max.length);
	}

	/**
	 * update the minimum and maximum extents with the given coordinates.
	 * @param position
	 */
	public void update(final long[] position) {
		for (int i = 0; i<min.length; i++) {
			if (position[i] < min[i]) min[i] = position[i];
			if (position[i] >= max[i]) max[i] = position[i]+1;
		}
	}
}
