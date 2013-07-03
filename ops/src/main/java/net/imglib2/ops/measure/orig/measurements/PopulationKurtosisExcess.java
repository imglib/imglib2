package net.imglib2.ops.measure.orig.measurements;

import net.imglib2.ops.measure.orig.Measurement;



public class PopulationKurtosisExcess implements Measurement {
	private PopulationKurtosis popKurt;
	
	public PopulationKurtosisExcess(PopulationKurtosis popKurt) {
		this.popKurt = popKurt;
	}

	@Override
	public double getValue() {
		return popKurt.getValue() - 3;
	}
	
}

