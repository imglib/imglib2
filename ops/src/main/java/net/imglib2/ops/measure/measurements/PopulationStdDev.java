package net.imglib2.ops.measure.measurements;

import net.imglib2.ops.measure.Measurement;



public class PopulationStdDev implements Measurement {

	private PopulationVariance popVar;
	
	public PopulationStdDev(PopulationVariance popVar) {
		this.popVar = popVar;
	}
	
	@Override
	public double getValue() {
		return Math.sqrt(popVar.getValue());
	}

}

