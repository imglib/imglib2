package net.imglib2.ops.measure.measurements;

import net.imglib2.ops.measure.Measurement;



public class PopulationVariance implements Measurement {

	private Moment2AboutMean moment2; 

	public PopulationVariance(Moment2AboutMean moment2) {
		this.moment2 = moment2;
	}

	@Override
	public double getValue() {
		return moment2.getValue();
	}
	
}

