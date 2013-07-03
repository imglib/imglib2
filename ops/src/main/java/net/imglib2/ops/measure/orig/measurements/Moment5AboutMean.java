package net.imglib2.ops.measure.orig.measurements;

import net.imglib2.ops.measure.orig.Measurement;



public class Moment5AboutMean implements Measurement {
	private Sum5AboutMean sum;
	private ElementCount numElems;

	public Moment5AboutMean(Sum5AboutMean sum, ElementCount numElems) {
		this.sum = sum;
		this.numElems = numElems;
	}
	
	@Override
	public double getValue() {
		return sum.getValue() / numElems.getValue();
	}
	
}

