package net.imglib2.ops;


public class DiscreteIterator {

	private boolean invalid;
	private long[] ctr;
	private long[] negOffs;
	private long[] posOffs;
	private long[] position;
	private long[] maxes;
	private long[] mins;
	
	public DiscreteIterator(long[] ctr, long[] negOffs, long[] posOffs) {
		this.ctr = ctr.clone();
		this.negOffs = negOffs;
		this.posOffs = posOffs;
		this.position = new long[ctr.length];
		this.mins = new long[ctr.length];
		this.maxes = new long[ctr.length];
		setMinsAndMaxes();
		reset();
	}

	public boolean hasNext() {
		if (invalid) return true;
		for (int i = 0; i < position.length; i++) {
			if (position[i] < maxes[i])
				return true;
		}
		return false;
	}
	
	public boolean hasPrev() {
		if (invalid) return true;
		for (int i = 0; i < position.length; i++) {
			if (position[i] > mins[i])
				return true;
		}
		return false;
	}
	
	public void fwd() {
		if (invalid) {
			first();
			invalid = false;
			return;
		}
		for (int i = 0; i < position.length; i++) {
			position[i]++;
			if (position[i] <= maxes[i]) return;
			position[i] = mins[i];
		}
		throw new IllegalArgumentException("can't move fwd() beyond end of region");
	}
	
	public void bck() {
		if (invalid) {
			last();
			invalid = false;
			return;
		}
		for (int i = 0; i < position.length; i++) {
			position[i]--;
			if (position[i] >= mins[i]) return;
			position[i] = maxes[i];
		}
		throw new IllegalArgumentException("can't move bck() before start of region");
	}
	
	public void reset() {
		invalid = true;
	}
	
	public long[] getPosition() {
		return position;
	}
	
	public void moveTo(long[] keyPt) {
		for (int i = 0; i < ctr.length; i++)
			ctr[i] = keyPt[i];
		setMinsAndMaxes();
		reset();
	}

	// -- private helpers --
	
	private void first() {
		for (int i = 0; i < position.length; i++)
			position[i] = mins[i];
	}
	
	private void last() {
		for (int i = 0; i < position.length; i++)
			position[i] = maxes[i];
	}
	
	private void setMinsAndMaxes() {
		for (int i = 0; i < ctr.length; i++) {
			mins[i] = ctr[i] - negOffs[i];
			maxes[i] = ctr[i] + posOffs[i];
		}
	}
}
