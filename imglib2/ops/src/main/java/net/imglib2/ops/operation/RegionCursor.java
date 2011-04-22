package net.imglib2.ops.operation;

import net.imglib2.Cursor;
import net.imglib2.type.numeric.RealType;

public class RegionCursor<K extends RealType<K>> {
	private Cursor<K> cursor;
	private long[] minCoords;
	private long[] maxCoords;
	
	public RegionCursor(Cursor<K> cursor, long[] origin, long[] span) {
		this.cursor = cursor;
		this.minCoords = origin.clone();
		this.maxCoords = new long[origin.length];
		for (int i = 0; i < origin.length; i++)
			this.maxCoords[i] = origin[i] + span[i] - 1;
	}

	private boolean cursorOutsideRegion() {
		for (int i = 0; i < this.minCoords.length; i++) {
			long indexVal = cursor.getLongPosition(i);
			if ((indexVal < this.minCoords[i]) || (indexVal > this.maxCoords[i]))
				return true;
		}
		return false;
	}
	
	public boolean hasNext() {
		int numDims = cursor.numDimensions();
		for (int i = 0; i < numDims; i++) {
			if (cursor.getLongPosition(i) < maxCoords[i])
				return true;
		}

		return false;
	}
	
	public void fwd() {
		cursor.fwd();
		while (cursorOutsideRegion() && cursor.hasNext())
			cursor.fwd();
	}
	
	public K get() {
		return cursor.get();
	}
	
	public void getPosition(long[] position) {
		for (int i = 0; i < position.length; i++)
			position[i] = cursor.getLongPosition(i);
	}
}
