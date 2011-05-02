package net.imglib2.ops.operation;

import net.imglib2.RandomAccess;
import net.imglib2.type.numeric.RealType;

// Kind of in a predicament
//   -- need to be able to set the origin of a region so need accessor's ability
//   -- want to be able to use fwd() like a cursor  (especially for performance)
//   could have two tracking variables - one of each (slow?)
//   could use a cursor and call fwd(long steps) appropriately assuming fwd()
//     is predictable. And depending on how fwd() works knowing which axis gets
//     crossed and calling fwd(delta) with a correct delta)
//   could use an accessor and copy Index style increment code from ImageJ2.
//     But I think that code is slow.
//   also note the hasNext() and fwd() definition as formulated will cause 1st
//     pixel value to be skipped
//   one good thing about random accessor idea: shape compatible regions are
//     walked in the exact same order depsite differences in dimensionality.
//     I.e. a 2d XY plane in a 2d image and a 2d XY plane in a 5d image.

public class RegionCursor<K extends RealType<K>> {
	private final RandomAccess<K> accessor;
	private final long[] minCoords;
	private final long[] maxCoords;
	private final long[] currCoords;
	private final int totalDims;
	
	public RegionCursor(RandomAccess<K> accessor, long[] origin, long[] span) {
		this.totalDims = accessor.numDimensions();
		if (origin.length != span.length)
			throw new IllegalArgumentException("origin and span are of differing dimension lengths");
		
		if (totalDims != origin.length)
			throw new IllegalArgumentException("accessor and origin/span are of differing dimension lengths");
		
		for (long dim : span)
			if (dim < 1)
				throw new IllegalArgumentException("span cannot have any dimension less than 1");
		
		this.accessor = accessor;
		this.minCoords = origin.clone();
		this.currCoords = this.minCoords.clone();
		this.maxCoords = new long[this.totalDims];
		for (int i = 0; i < this.totalDims; i++) {
			this.maxCoords[i] = origin[i] + span[i] - 1;
		}
	}

	public void getPosition(long[] index) {
		for (int i = 0; i < this.totalDims; i++)
			index[i] = this.currCoords[i];
	}

	public K getValue() {
		return this.accessor.get();
	}

	public boolean isValid() {
		for (int i = 0; i < this.totalDims; i++) {
			long indexVal = this.currCoords[i];
			if ((indexVal < this.minCoords[i]) || (indexVal > this.maxCoords[i]))
				return false;
		}
		return true;
	}
	
	public void next() {
		final int lastDim = this.totalDims-1;
		for (int i = 0; i < this.totalDims; i++) {
			this.currCoords[i]++;
			if (this.currCoords[i] <= this.maxCoords[i]) {
				this.accessor.setPosition(this.currCoords);
				return;
			}
			// else currCoord[i] > maxCoord[i]
			if (i == lastDim) { // can't increment anymore?
				return;           // then return pointing out of bounds
			}
			this.currCoords[i] = this.minCoords[i];
		}
	}
	
	public void reset() {
		for (int i = 0; i < this.totalDims; i++)
			this.currCoords[i] = this.minCoords[i];
		this.accessor.setPosition(this.currCoords);
	}
	
}
