package net.imglib2.ops.measure.orig.measurements;

import net.imglib2.ops.measure.orig.Measurement;



public class Moment5AboutZero implements Measurement {
	private Sum5 sum;
	private ElementCount numElems;

	public Moment5AboutZero(Sum5 sum, ElementCount numElems) {
		this.sum = sum;
		this.numElems = numElems;
	}

	@Override
	public double getValue() {
		return sum.getValue() / numElems.getValue();
	}
	
}

