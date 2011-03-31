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

package mpicbg.imglib.labeling.array;

import java.util.Collection;

import mpicbg.imglib.img.array.ArrayImg;
import mpicbg.imglib.img.basictypeaccess.IntAccess;
import mpicbg.imglib.labeling.LabelingMapping;
import mpicbg.imglib.labeling.LabelingType;
import mpicbg.imglib.labeling.NativeLabeling;
import mpicbg.imglib.roi.IterableRegionOfInterest;
import mpicbg.imglib.roi.RegionOfInterest;


/**
 * A labeling backed by a raster array
 * @author leek
 *
 * @param <T> the type of labels assigned to pixels
 * @param <A> the container type
 */
public class ArrayLabeling<T extends Comparable<T>, A extends IntAccess> 
	extends ArrayImg<LabelingType<T>, A> implements NativeLabeling<T, A>{

	public ArrayLabeling(A data, long[] dim) {
		super(data, dim, 1);
	}

	@Override
	public boolean getExtents(T label, long[] minExtents, long[] maxExtents) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getRasterStart(T label, long[] start) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long getArea(T label) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Collection<T> getLabels() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RegionOfInterest getRegionOfInterest(T label) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IterableRegionOfInterest getIterableRegionOfInterest(T label) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LabelingMapping<T, Integer> getMapping() {
		return this.linkedType.getMapping();
	}
}
