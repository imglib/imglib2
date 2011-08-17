package net.imglib2.ops;

import java.lang.reflect.Array;

public abstract class Neighborhood<ARRAY> {

	private ARRAY keyPoint;
	private ARRAY negOffsets;
	private ARRAY posOffsets;
	
	public Neighborhood(ARRAY keyPt, ARRAY negOffs, ARRAY posOffs) {
		keyPoint = keyPt;
		negOffsets = negOffs;
		posOffsets = posOffs;
		int len1 = Array.getLength(keyPt);
		int len2 = Array.getLength(negOffs);
		int len3 = Array.getLength(posOffs);
		if ((len1 != len2) || (len2 != len3))
			throw new IllegalArgumentException("Neighborhood poorly defined: input array lengths differ");
	}
	
	public ARRAY getKeyPoint() {
		return keyPoint;
	}

	public ARRAY getNegativeOffsets() {
		return negOffsets;
	}

	public int getNumDims() {
		return Array.getLength(keyPoint);
	}

	public ARRAY getPositiveOffsets() {
		return posOffsets;
	}

	public void moveTo(ARRAY newKeyPoint) {
		if (newKeyPoint != keyPoint) {
			if (Array.getLength(newKeyPoint) != Array.getLength(keyPoint))
				throw new IllegalArgumentException("moveTo() - new key point has wrong number of dimensions");
			keyPoint = newKeyPoint;
		}
	}
}

