package mpicbg.imglib.scripting.math;

import java.util.Set;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.fn.Operation;
import mpicbg.imglib.type.numeric.RealType;

public final class Random<R extends RealType<R>> implements Operation<R> {

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
	public final void compute(final R output) {
		output.setReal(rand.nextDouble());
	}

	@Override
	public final void fwd() {}

	@Override
	public final void getImages(final Set<Image<? extends RealType<?>>> images) {}

	@Override
	public final void init(final R ref) {}

	@Override
	public final void compute(final RealType<?> ignored1, final RealType<?> ignored2, final R output) {
		// Not needed, but perhaps it's called from elsewhere
		output.setReal(rand.nextDouble());
	}
}