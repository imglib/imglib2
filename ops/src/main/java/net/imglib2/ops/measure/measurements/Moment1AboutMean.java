package net.imglib2.ops.measure.measurements;

import net.imglib2.ops.measure.Measurement;



public class Moment1AboutMean implements Measurement {
	private Sum1AboutMean sum;
	private ElementCount numElems;

	public Moment1AboutMean(Sum1AboutMean sum, ElementCount numElems) {
		this.sum = sum;
		this.numElems = numElems;
	}
	
	@Override
	public double getValue() {
		return sum.getValue() / numElems.getValue();
	}
	
}

