package net.imglib2.ops;


public class ContinuousNeigh extends Neighborhood<double[]> {

	public ContinuousNeigh(double[] keyPt, double[] negOffs, double[] posOffs) {
		super(keyPt, negOffs, posOffs);
		// TODO - do this in base class
		for (int i = 0; i < keyPt.length; i++) {
			if ((negOffs[i] < 0) || (posOffs[i] < 0))
				throw new IllegalArgumentException("ContinuousNeigh() : offsets must be nonnegative in magnitude");
		}
	}

	public ContinuousNeigh duplicate() {
		return new ContinuousNeigh(
			getKeyPoint().clone(),
			getNegativeOffsets().clone(),
			getPositiveOffsets().clone());
	}
}

