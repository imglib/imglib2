package net.imglib2.ops.measure.measurements;

import net.imglib2.ops.measure.Measurement;



public class PopulationKurtosis implements Measurement {

	private Moment2AboutMean m2;
	private Moment4AboutMean m4;
	
	public PopulationKurtosis(Moment2AboutMean m2, Moment4AboutMean m4) {
		this.m2 = m2;
		this.m4 = m4;
	}

	@Override
	public double getValue() {
		double m2Val = m2.getValue();
		return m4.getValue() / (m2Val * m2Val);
	}
	
}


