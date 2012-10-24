package net.imglib2.ops.measure.measurements;

import net.imglib2.ops.measure.Measurement;



public class Moment3AboutMean implements Measurement {
	private Sum3AboutMean sum;
	private ElementCount numElems;

	public Moment3AboutMean(Sum3AboutMean sum, ElementCount numElems) {
		this.sum = sum;
		this.numElems = numElems;
	}
	
	@Override
	public double getValue() {
		return sum.getValue() / numElems.getValue();
	}
	
}

