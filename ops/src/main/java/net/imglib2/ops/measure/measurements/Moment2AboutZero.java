package net.imglib2.ops.measure.measurements;

import net.imglib2.ops.measure.Measurement;



public class Moment2AboutZero implements Measurement {
	private Sum2 sum;
	private ElementCount numElems;

	public Moment2AboutZero(Sum2 sum, ElementCount numElems) {
		this.sum = sum;
		this.numElems = numElems;
	}

	@Override
	public double getValue() {
		return sum.getValue() / numElems.getValue();
	}
	
}

