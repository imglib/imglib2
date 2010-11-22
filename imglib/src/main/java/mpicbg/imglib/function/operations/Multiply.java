package mpicbg.imglib.function.operations;

import java.util.Set;

import mpicbg.imglib.type.numeric.RealType;
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

public class Multiply< R extends RealType<R> > implements Operation<R>
{

	final Op<R> inner;

	public Multiply(final Image<? extends RealType<?>> left, final Image<? extends RealType<?>> right) {
		this.inner = new II<R>(left, right, this);
	}

	public Multiply(final Operation<R> op, final Image<? extends RealType<R>> right) {
		this.inner = new OpI<R>(op, right, this);
	}

	public Multiply(final Image<? extends RealType<?>> left, final Operation<R> op) {
		this.inner = new IOp<R>(left, op, this);
	}

	public Multiply(final Operation<R> op1, final Operation<R> op2) {
		this.inner = new OpOp<R>(op1, op2, this);
	}
	
	public Multiply(final Image<? extends RealType<?>> left, final Number val) {
		this.inner = new IN<R>(left, val, this);
	}

	public Multiply(final Number val,final Image<? extends RealType<?>> right) {
		this.inner = new NI<R>(val, right, this);
	}

	public Multiply(final Operation<R> left, final Number val) {
		this.inner = new OpN<R>(left, val, this);
	}

	public Multiply(final Number val,final Operation<R> right) {
		this.inner = new NOp<R>(val, right, this);
	}

	@Override
	public final void compute( final RealType<?> input1, final RealType<?> input2, final R output ) {
		/*
		output.set(input1);
		output.mul(input2);
		*/
		output.setReal(input1.getRealFloat() * input2.getRealFloat());
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
	public void getImages(final Set<Image<? extends RealType<?>>> images) {
		inner.getImages(images);
	}

	@Override
	public void init(final R ref) {
		inner.init(ref);
	}
}
