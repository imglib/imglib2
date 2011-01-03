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
import java.util.Iterator;

import mpicbg.imglib.container.Container;
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.cursor.special.RegionOfInterestCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.label.FakeType;

/**
 * A cursor that only visits labeled pixels. The cursor is defined on a
 * localizable cursor.
 * 
 * @param <T> - the label type
 */
public class LocalizableLabelingCursor<T extends Comparable<T>> implements LocalizableCursor<FakeType>{
	final protected LocalizableCursor<LabelingType<T>> c;
	final protected FakeType fakeType = new FakeType();
	int [] offset;
	protected int [] currentPosition;
	protected int arrayIndex;
	protected int storageIndex;
	protected T label;
	protected boolean advanced;
	protected boolean has_next;
	
	public LocalizableLabelingCursor(LocalizableCursor<LabelingType<T>> c, T label) {
		this.c = c;
		this.label = label;
		this.offset = c.getPosition();
		Arrays.fill(this.offset, 0);
		currentPosition = c.createPositionArray();
		Arrays.fill(currentPosition, Integer.MIN_VALUE);
		advanced = false;
	}
	
	public LocalizableLabelingCursor(RegionOfInterestCursor<LabelingType<T>> c, int [] offset, T label) {
		this(c, label);
		this.offset = offset;
	}
	protected boolean isLabeled() {
		LabelingType<T> type = c.getType();
		for (T pixelLabel: type.getLabeling()) {
			if (pixelLabel.equals(label)) {
				return true;
			}
		}
		return false;
	}
	
	protected void getCursorPosition(int [] position) {
		c.getPosition(position);
		for (int i=0;i<position.length;i++) {
			position[i] += offset[i];
		}
	}
	protected void advance() {
		if (advanced) return;
		arrayIndex = c.getArrayIndex();
		storageIndex = c.getStorageIndex();
		getCursorPosition(currentPosition);
		while (c.hasNext()) {
			c.next();
			if (isLabeled()) {
				advanced = true;
				has_next = true;
				return;
			}
		}
		has_next = false;
		advanced = true;
	}
	@Override
	public void reset() {
		c.reset();
		advanced = false;
	}

	@Override
	public boolean isActive() {
		// TODO Auto-generated method stub
		return c.isActive();
	}

	@Override
	public Image<FakeType> getImage() {
		return null;
	}

	@Override
	public FakeType getType() {
		return fakeType;
	}

	@Override
	public int getArrayIndex() {
		advance();
		return arrayIndex;
	}

	@Override
	public int getStorageIndex() {
		advance();
		return storageIndex;
	}

	@Override
	public Container<FakeType> getStorageContainer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDebug(boolean debug) {
		c.setDebug(debug);
		
	}

	@Override
	public int[] createPositionArray() {
		// TODO Auto-generated method stub
		return c.createPositionArray();
	}

	@Override
	public void close() {
	}

	@Override
	public boolean hasNext() {
		advance();
		return has_next;
	}

	@Override
	public FakeType next() {
		fwd();
		return fakeType;
	}

	@Override
	public void remove() {
		
	}

	@Override
	public Iterator<FakeType> iterator() {
		reset();
		return this;
	}

	@Override
	public void fwd(long steps) {
		for (long i=0; (i<steps) && hasNext() ; i++) fwd(); 
	}

	@Override
	public void fwd() {
		advanced = false;
	}

	@Override
	public int getNumDimensions() {
		return c.getNumDimensions();
	}

	@Override
	public int[] getDimensions() {
		return c.getDimensions();
	}

	@Override
	public void getDimensions(int[] position) {
		c.getDimensions(position);
	}

	@Override
	public void getPosition(int[] position) {
		advance();
		System.arraycopy(this.currentPosition, 0, position, 0, currentPosition.length);
	}

	@Override
	public int[] getPosition() {
		advance();
		return currentPosition.clone();
	}

	@Override
	public int getPosition(int dim) {
		advance();
		return currentPosition[dim];
	}

	@Override
	public String getPositionAsString() {
		advance();
		StringBuilder sb= new StringBuilder(Integer.toString(currentPosition[0]));
		for (int i=1; i<currentPosition.length; i++) {
			sb.append(',');
			sb.append(currentPosition[i]);
		}
		return sb.toString();
	}

}
