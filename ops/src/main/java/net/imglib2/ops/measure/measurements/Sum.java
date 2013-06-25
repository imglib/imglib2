package net.imglib2.ops.measure.measurements;

import net.imglib2.ops.measure.Measurement;

public class Sum implements Measurement {

	private Sum1 sum;
	
	public Sum(Sum1 sum) {
		this.sum = sum;
	}
	
	@Override
	public double getValue() {
		return sum.getValue();
	}

}
