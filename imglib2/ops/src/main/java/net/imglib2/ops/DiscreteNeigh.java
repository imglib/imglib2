package net.imglib2.ops;

public class DiscreteNeigh extends Neighborhood<long[]> {

	private DiscreteIterator iterator;
	
	public DiscreteNeigh(long[] keyPt, long[] negOffs, long[] posOffs) {
		super(keyPt, negOffs, posOffs);
		// TODO - do this in base class
		for (int i = 0; i < keyPt.length; i++) {
			if ((negOffs[i] < 0) || (posOffs[i] < 0))
				throw new IllegalArgumentException("DiscreteNeigh() : offsets must be nonnegative in magnitude");
		}
		iterator = null; // create lazily: speeds moveTo()
	}
	
	public DiscreteNeigh duplicate() {
		return new DiscreteNeigh(
			getKeyPoint().clone(),
			getNegativeOffsets().clone(),
			getPositiveOffsets().clone());
	}
	
	@Override
	public void moveTo(long[] newKeyPoint) {
		super.moveTo(newKeyPoint);
		if (iterator != null)
			iterator.moveTo(newKeyPoint);
	}
	
	public DiscreteIterator getIterator() {
		if (iterator == null)
			iterator = new DiscreteIterator(
				getKeyPoint(), getNegativeOffsets(), getPositiveOffsets());
		return iterator;
	}
	
	// TODO - restrict axis ranges one at a time. do here or in superclass.
}

