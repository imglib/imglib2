package net.imglib2.ops.operator.unary;

import net.imglib2.ops.operator.UnaryOperator;

import java.util.Random;

public final class AddNoise implements UnaryOperator
{
	private final double min, max, stdDev;
	private final Random rng;
	
	public AddNoise(final double min, final double max, final double stdDev)
	{
		this.min = min;
		this.max = max;
		this.stdDev = stdDev;
		this.rng = new Random();
		this.rng.setSeed(System.currentTimeMillis());
	}
	
	@Override
	public double computeValue(final double input)
	{
		int i = 0;
		do
		{
			double newVal = input + (rng.nextGaussian() * stdDev);
			
			if ((min <= newVal) && (newVal <= max))
				return newVal;
			
			if (i++ > 100)
				throw new IllegalArgumentException("noise function failing to terminate. probably misconfigured.");
		}
		while(true);
	}
	
}
