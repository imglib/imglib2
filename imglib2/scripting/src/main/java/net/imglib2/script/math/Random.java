package net.imglib2.script.math;

import net.imglib2.script.math.fn.IFunction;
import net.imglib2.script.math.fn.NumberFunction;
import net.imglib2.script.math.fn.UnaryOperation;

public class Random extends UnaryOperation
{
	private final java.util.Random rand;

	@SuppressWarnings("boxing")
	public Random() {
		this(System.currentTimeMillis());
	}

	public Random(final Number seed) {
		super(seed);
		rand = new java.util.Random(seed.longValue());
	}
	
	public Random(final NumberFunction seed) {
		super(seed);
		rand = new java.util.Random((long)seed.eval());
	}
	
	@SuppressWarnings("boxing")
	public Random(final java.util.Random rand) {
		super(0); // bogus
		this.rand = rand;
	}

	@Override
	public final double eval() {
		return rand.nextDouble();
	}
	
	/** Duplicates this IFunction but not the random number generator. */
	@Override
	public IFunction duplicate() {
		return new Random(this.rand);
	}
}
