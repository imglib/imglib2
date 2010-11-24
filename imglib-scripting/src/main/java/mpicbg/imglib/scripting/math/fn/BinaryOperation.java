package mpicbg.imglib.scripting.math.fn;

import java.util.Set;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.op.II;
import mpicbg.imglib.scripting.math.op.IN;
import mpicbg.imglib.scripting.math.op.IOp;
import mpicbg.imglib.scripting.math.op.NI;
import mpicbg.imglib.scripting.math.op.NN;
import mpicbg.imglib.scripting.math.op.NOp;
import mpicbg.imglib.scripting.math.op.Op;
import mpicbg.imglib.scripting.math.op.OpI;
import mpicbg.imglib.scripting.math.op.OpN;
import mpicbg.imglib.scripting.math.op.OpOp;
import mpicbg.imglib.type.numeric.RealType;

public abstract class BinaryOperation<R extends RealType<R>> implements Operation<R>, FunctionReal<R>
{
	private final Op<R> inner;

	public BinaryOperation(final Image<? extends RealType<?>> left, final Image<? extends RealType<?>> right) {
		this.inner = new II<R>(left, right, this);
	}

	public BinaryOperation(final Operation<R> op, final Image<? extends RealType<?>> right) {
		this.inner = new OpI<R>(op, right, this);
	}

	public BinaryOperation(final Image<? extends RealType<?>> left, final Operation<R> op) {
		this.inner = new IOp<R>(left, op, this);
	}

	public BinaryOperation(final Operation<R> op1, final Operation<R> op2) {
		this.inner = new OpOp<R>(op1, op2, this);
	}
	
	public BinaryOperation(final Image<? extends RealType<?>> left, final Number val) {
		this.inner = new IN<R>(left, val, this);
	}

	public BinaryOperation(final Number val,final Image<? extends RealType<?>> right) {
		this.inner = new NI<R>(val, right, this);
	}

	public BinaryOperation(final Operation<R> left, final Number val) {
		this.inner = new OpN<R>(left, val, this);
	}

	public BinaryOperation(final Number val,final Operation<R> right) {
		this.inner = new NOp<R>(val, right, this);
	}
	
	public BinaryOperation(final Number val1, final Number val2) {
		this.inner = new NN<R>(val1, val2, this);
	}
	
	@Override
	public void fwd() {
		inner.fwd();
	}

	@Override
	public void compute(final R output) {
		inner.compute(output);
	}

	@Override
	public void getImages(final Set<Image<?>> images) {
		inner.getImages(images);
	}

	@Override
	public void init(final R ref) {
		inner.init(ref);
	}
}