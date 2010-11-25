package mpicbg.imglib.scripting.math;

import mpicbg.imglib.scripting.math.fn.UnaryOperation;

public class Random extends UnaryOperation
{
	private final java.util.Random rand;

	public Random() {
		this(System.currentTimeMillis());
	}

	public Random(final Number seed) {
		super(seed);
		rand = new java.util.Random(seed.longValue());
	}

	@Override
	public final double eval() {
		return rand.nextDouble();
	}
}