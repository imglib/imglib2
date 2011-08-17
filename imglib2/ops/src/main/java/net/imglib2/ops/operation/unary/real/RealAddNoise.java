package net.imglib2.ops.operation.unary.real;

import java.util.Random;

import net.imglib2.ops.Real;
import net.imglib2.ops.UnaryOperation;


public class RealAddNoise implements UnaryOperation<Real> {

	private double rangeMin;
	private double rangeMax;
	private double rangeStdDev;
	private Random rng;
	
	public RealAddNoise(double min, double max, double stdDev) {
		this.rangeMin = min;
		this.rangeMax = max;
		this.rangeStdDev = stdDev;
		this.rng = new Random();
		this.rng.setSeed(System.currentTimeMillis());
	}
	
	@Override
	public void compute(Real input, Real output) {
		int i = 0;
		do
		{
			double newVal = input.getReal() + (rng.nextGaussian() * rangeStdDev);
			
			if ((rangeMin <= newVal) && (newVal <=rangeMax)) {
				output.setReal(newVal);
				return;
			}
			
			if (i++ > 100)
				throw new IllegalArgumentException("noise function failing to terminate. probably misconfigured.");
		}
		while(true);
	}

}
