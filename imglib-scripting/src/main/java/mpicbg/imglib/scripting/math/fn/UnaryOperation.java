package mpicbg.imglib.scripting.math.fn;

import java.util.Set;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.fn.Operation;
import mpicbg.imglib.scripting.math.op.None;
import mpicbg.imglib.scripting.math.op.Op;
import mpicbg.imglib.scripting.math.op.SingleI;
import mpicbg.imglib.scripting.math.op.SingleN;
import mpicbg.imglib.scripting.math.op.SingleOp;
import mpicbg.imglib.type.numeric.RealType;

public abstract class UnaryOperation< R extends RealType<R> > implements Operation<R>, FunctionReal<R>
{
	private final Op<R> inner;

	/** For a generative operation, such as Random(). */
	public UnaryOperation() {
		this.inner = new None<R>(this);
	}
	
	public UnaryOperation(final Image<? extends RealType<?>> img) {
		this.inner = new SingleI<R>(img, this);
	}

	public UnaryOperation(final Operation<R> op) {
		this.inner = new SingleOp<R>(op, this);
	}

	public UnaryOperation(final Number val) {
		this.inner = new SingleN<R>(val, this);
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