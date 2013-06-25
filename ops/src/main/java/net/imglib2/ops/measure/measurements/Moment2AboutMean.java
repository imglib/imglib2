package net.imglib2.ops.measure.measurements;

import net.imglib2.ops.measure.Measurement;



public class Moment2AboutMean implements Measurement {
	private Sum2AboutMean sum;
	private ElementCount numElems;

	public Moment2AboutMean(Sum2AboutMean sum, ElementCount numElems) {
		this.sum = sum;
		this.numElems = numElems;
	}
	
	@Override
	public double getValue() {
		return sum.getValue() / numElems.getValue();
	}
	
}

