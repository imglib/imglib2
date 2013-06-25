package net.imglib2.ops.measure.measurements;

import net.imglib2.ops.measure.Measurement;



public class Moment4AboutZero implements Measurement {
	private Sum4 sum;
	private ElementCount numElems;

	public Moment4AboutZero(Sum4 sum, ElementCount numElems) {
		this.sum = sum;
		this.numElems = numElems;
	}

	@Override
	public double getValue() {
		return sum.getValue() / numElems.getValue();
	}
	
}

