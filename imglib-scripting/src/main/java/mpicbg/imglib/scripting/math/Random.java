package mpicbg.imglib.scripting.math;

import mpicbg.imglib.scripting.math.fn.UnaryOperation;
import mpicbg.imglib.type.numeric.RealType;

public final class Random<R extends RealType<R>> extends UnaryOperation<R> {

	private final java.util.Random rand;
	
	public Random() {
		this.rand = new java.util.Random();
	}

	public Random(final long seed) {
		this.rand = new java.util.Random(seed);
	}
	
	public Random(final double seed) {
		this((long)seed);
	}

	@Override
	public final void compute(final RealType<?> ignored1, final RealType<?> ignored2, final R output) {
		output.setReal(rand.nextDouble());
	}
}