package net.imglib2.ops.measure.orig.measurements;

import net.imglib2.ops.measure.orig.Measurement;



public class Moment1AboutZero implements Measurement {
	private Sum1 sum;
	private ElementCount numElems;

	public Moment1AboutZero(Sum1 sum, ElementCount numElems) {
		this.sum = sum;
		this.numElems = numElems;
	}

	@Override
	public double getValue() {
		return sum.getValue() / numElems.getValue();
	}
	
}

