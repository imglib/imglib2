package net.imglib2.ops.measure.measurements;

import net.imglib2.ops.measure.Measurement;



public class Moment4AboutMean implements Measurement {
	private Sum4AboutMean sum;
	private ElementCount numElems;

	public Moment4AboutMean(Sum4AboutMean sum, ElementCount numElems) {
		this.sum = sum;
		this.numElems = numElems;
	}
	
	@Override
	public double getValue() {
		return sum.getValue() / numElems.getValue();
	}
	
}

