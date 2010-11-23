package mpicbg.imglib.scripting.math;

import java.util.Set;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.fn.Operation;
import mpicbg.imglib.scripting.math.op.Op;
import mpicbg.imglib.scripting.math.op.SingleI;
import mpicbg.imglib.scripting.math.op.SingleN;
import mpicbg.imglib.scripting.math.op.SingleOp;
import mpicbg.imglib.type.numeric.RealType;

/** Returns the double value that is closest in value to the argument and is equal to a mathematical integer. */
public class Rint< R extends RealType<R> > implements Operation<R> {

	private final Op<R> inner;

	public Rint(final Image<? extends RealType<?>> img) {
		this.inner = new SingleI<R>(img, this);
	}

	public Rint(final Operation<R> op) {
		this.inner = new SingleOp<R>(op, this);
	}

	public Rint(final Number val) {
		this.inner = new SingleN<R>(val, this);
	}

	@Override
	public final void compute( final RealType<?> input1, final RealType<?> ignored, final R output ) {
		output.setReal(Math.rint(input1.getRealDouble()));
	}

	@Override
	public final void fwd() {
		inner.fwd();
	}

	@Override
	public final void compute(final R output) {
		inner.compute(output);
	}

	@Override
	public final void getImages(final Set<Image<?>> images) {
		inner.getImages(images);
	}

	@Override
	public final void init(final R ref) {
		inner.init(ref);
	}
}

