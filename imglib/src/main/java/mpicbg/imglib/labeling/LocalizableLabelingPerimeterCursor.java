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

import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.cursor.special.RegionOfInterestCursor;

public class LocalizableLabelingPerimeterCursor<T extends Comparable<T>>
		extends LocalizableLabelingCursor<T> {

	LocalizableByDimCursor<LabelingType<T>> cc;
	int [] dimensions;
	int [] position;
	public LocalizableLabelingPerimeterCursor(
			LocalizableCursor<LabelingType<T>> c,
			LocalizableByDimCursor<LabelingType<T>> cc, T label) {
		super(c, label);
		this.cc = cc;
		dimensions = cc.getDimensions();
		position = c.createPositionArray();
	}

	public LocalizableLabelingPerimeterCursor(
			RegionOfInterestCursor<LabelingType<T>> c, int[] offset,
			LocalizableByDimCursor<LabelingType<T>> cc, T label) {
		super(c, offset, label);
		this.cc = cc;
		dimensions = cc.getDimensions();
		position = c.createPositionArray();
	}
	@Override
	protected boolean isLabeled() {
		if (! super.isLabeled()) {
			return false;
		}
		/*
		 * Check to see if the current location is 4-connected to an
		 * unlabeled pixel.
		 */
		getCursorPosition(position);
		for (int i = 0; i < position.length; i++) {
			/*
			 * At each coordinate, try the pixel +/- 1
			 */
			for (int j = -1; j <=1; j+= 2) {
				if ((j == -1) && (position[i] == 0)) continue;
				if ((j == 1) && (position[i] == dimensions[i]-1)) continue;
				position[i] += j;
				cc.setPosition(position);
				position[i] -= j;
				boolean match = false;
				for (T l:cc.getType().getLabeling() ) {
					if (l.equals(this.label)) {
						match = true;
						break;
					}
				}
				if (! match) return true;
			}
		}
		return false;
	}
}
