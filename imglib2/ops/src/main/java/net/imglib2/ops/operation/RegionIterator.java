package net.imglib2.ops.operation;

import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;

// Kind of in a predicament
//   -- need to be able to set the origin of a region so need accessor's ability
//   -- want to be able to use fwd() like a cursor  (especially for performance)
//   could have two tracking variables - one of each (slow?)
//   could use a cursor and call fwd(long steps) appropriately assuming fwd()
//     is predictable. And depending on how fwd() works knowing which axis gets
//     crossed and calling fwd(delta) with a correct delta)
//   could use an accessor and copy Index style increment code from ImageJ2.
//     But I think that code is slow.
//   one good thing about random accessor idea: shape compatible regions are
//     walked in the exact same order depsite differences in dimensionality.
//     I.e. a 2d XY plane in a 2d image and a 2d XY plane in a 5d image.

import java.util.Iterator;

public class RegionIterator<K extends RealType<K>> implements Iterator<K> {
	private final RandomAccess<K> accessor;
	private final long[] minCoords;
	private final long[] maxCoords;
	private final long[] currCoords;
	private final int totalDims;
	private boolean haveNotIncremented;
	
	public RegionIterator(RandomAccess<K> accessor, long[] origin, long[] span) {
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
		resetInternals();
	}

	public void getPosition(long[] index) {
		for (int i = 0; i < this.totalDims; i++)
			index[i] = this.currCoords[i];
	}

	public K getValue() {
		return this.accessor.get();
	}

	@Override
	public boolean hasNext() {
		if (this.haveNotIncremented)
			return true;
		for (int i = 0; i < this.totalDims; i++) {
			if (this.currCoords[i] < this.maxCoords[i])
				return true;
		}
		return false;
	}

	@Override
	public K next() {
		incIndex();
		return this.accessor.get();
	}
	

	@Override
	public void remove() {
		throw new UnsupportedOperationException("remove() not implemented for RegionIterator");
	}
	
	public void reset() {
		resetInternals();
	}

	// -- helpers --

	private void incIndex() {
		if (this.haveNotIncremented) {
			this.haveNotIncremented = false;
			return;
		}
		final int lastDim = this.totalDims-1;
		for (int i = 0; i < this.totalDims; i++) {
			this.currCoords[i]++;
			if (this.currCoords[i] <= this.maxCoords[i]) {
				// NEW WAY
				this.accessor.move(1,i);
				// OLD WAY - confirmed about 15% slower
				// this.accessor.setPosition(this.currCoords);
				return;
			}
			// else currCoord[i] > maxCoord[i]
			if (i == lastDim) { // can't increment anymore?
				return;           // then return pointing out of bounds
			}
			this.currCoords[i] = this.minCoords[i];
			// NEW WAY
			this.accessor.move((this.minCoords[i] - this.maxCoords[i]), i);
				// note: replacing this above subtraction with lookup in a precomputed
				//   table is significantly (30%) slower!
		}
	}

	private void resetInternals() {
		this.haveNotIncremented = true;
		for (int i = 0; i < this.totalDims; i++)
			this.currCoords[i] = this.minCoords[i];
		this.accessor.setPosition(this.currCoords);
	}
	
	// -- speed test code --
	
	public static void main(String[] args) {
		//do a speed test
		final int DimSize = 100;
		ArrayImgFactory<UnsignedByteType> factory =
			new ArrayImgFactory<UnsignedByteType>();
		Img<UnsignedByteType> data =
			factory.create(new long[]{DimSize,DimSize,DimSize},
				new UnsignedByteType());
		long startTime = System.currentTimeMillis();
		RegionIterator<UnsignedByteType> cursor =
			new RegionIterator<UnsignedByteType>(data.randomAccess(),
					new long[]{2,3,4}, new long[]{DimSize-7, DimSize-5, DimSize-3});
		for (int i = 0; i < 1000; i++) {
			cursor.reset();
			while (cursor.hasNext())
				cursor.next();
		}
		long endTime = System.currentTimeMillis();
		System.out.println("Total time in millis = "+(endTime-startTime));
	}

}
