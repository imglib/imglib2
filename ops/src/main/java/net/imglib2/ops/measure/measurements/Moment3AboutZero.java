package net.imglib2.ops.measure.measurements;

import net.imglib2.ops.measure.Measurement;



public class Moment3AboutZero implements Measurement {
	private Sum3 sum;
	private ElementCount numElems;

	public Moment3AboutZero(Sum3 sum, ElementCount numElems) {
		this.sum = sum;
		this.numElems = numElems;
	}

	@Override
	public double getValue() {
		return sum.getValue() / numElems.getValue();
	}
	
}

