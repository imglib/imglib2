package mpicbg.imglib.function.operations;

import java.util.Set;

import mpicbg.imglib.type.numeric.NumericType;
import mpicbg.imglib.function.operations.op.Op;
import mpicbg.imglib.function.operations.op.OpII;
import mpicbg.imglib.function.operations.op.OpIOp;
import mpicbg.imglib.function.operations.op.OpOpI;
import mpicbg.imglib.function.operations.op.OpOpOp;
import mpicbg.imglib.image.Image;

public class Subtract< A extends NumericType<A> > implements Operation<A>
{

	final Op<A> inner;

	public Subtract(final Image<A> left, final Image<A> right) {
		this.inner = new OpII<A>(left, right, this);
	}

	public Subtract(final Operation<A> op, final Image<A> right) {
		this.inner = new OpOpI<A>(op, right, this);
	}

	public Subtract(final Image<A> left, final Operation<A> op) {
		//this(op, left);
		this.inner = new OpIOp<A>(left, op, this);
	}

	public Subtract(final Operation<A> op1, final Operation<A> op2) {
		this.inner = new OpOpOp<A>(op1, op2, this);
	}

	@Override
	public final void compute( final A input1, final A input2, final A output ) {
		output.set(input1);
		output.sub(input2);
	}

	@Override
	public final void fwd() {
		inner.fwd();
	}

	@Override
	public final void compute(final A output) {
		inner.compute(output);
	}

	@Override
	public void getImages(final Set<Image<A>> images) {
		inner.getImages(images);
	}
}