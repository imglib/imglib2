package mpicbg.imglib.function.operations;

import java.util.Set;

import mpicbg.imglib.type.numeric.NumericType;
import mpicbg.imglib.function.operations.op.IN;
import mpicbg.imglib.function.operations.op.NI;
import mpicbg.imglib.function.operations.op.NOp;
import mpicbg.imglib.function.operations.op.Op;
import mpicbg.imglib.function.operations.op.II;
import mpicbg.imglib.function.operations.op.IOp;
import mpicbg.imglib.function.operations.op.OpI;
import mpicbg.imglib.function.operations.op.OpN;
import mpicbg.imglib.function.operations.op.OpOp;
import mpicbg.imglib.image.Image;

public class Subtract< A extends NumericType<A> > implements Operation<A>
{

	final Op<A> inner;

	public Subtract(final Image<A> left, final Image<A> right) {
		this.inner = new II<A>(left, right, this);
	}

	public Subtract(final Operation<A> op, final Image<A> right) {
		this.inner = new OpI<A>(op, right, this);
	}

	public Subtract(final Image<A> left, final Operation<A> op) {
		//this(op, left);
		this.inner = new IOp<A>(left, op, this);
	}

	public Subtract(final Operation<A> op1, final Operation<A> op2) {
		this.inner = new OpOp<A>(op1, op2, this);
	}
	
	public Subtract(final Image<A> left, final Number val) {
		this.inner = new IN<A>(left, val, this);
	}

	public Subtract(final Number val,final Image<A> right) {
		this.inner = new NI<A>(val, right, this);
	}

	public Subtract(final Operation<A> left, final Number val) {
		this.inner = new OpN<A>(left, val, this);
	}

	public Subtract(final Number val,final Operation<A> right) {
		this.inner = new NOp<A>(val, right, this);
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

	@Override
	public void init(final A ref) {
		inner.init(ref);
	}
}