package mpicbg.imglib.scripting.math2.fn;

import java.util.Set;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.RealType;

public abstract class BinaryOperation implements IFunction
{
	protected final IFunction a, b;

	public BinaryOperation(final Image<? extends RealType<?>> left, final Image<? extends RealType<?>> right) {
		this.a = new ImageFunction(left);
		this.b = new ImageFunction(right);
	}

	public BinaryOperation(final IFunction fn, final Image<? extends RealType<?>> right) {
		this.a = fn;
		this.b = new ImageFunction(right);
	}

	public BinaryOperation(final Image<? extends RealType<?>> left, final IFunction fn) {
		this.a = new ImageFunction(left);
		this.b = fn;
	}

	public BinaryOperation(final IFunction fn1, final IFunction fn2) {
		this.a = fn1;
		this.b = fn2;
	}

	public BinaryOperation(final Image<? extends RealType<?>> left, final Number val) {
		this.a = new ImageFunction(left);
		this.b = new NumberFunction(val);
	}

	public BinaryOperation(final Number val,final Image<? extends RealType<?>> right) {
		this.a = new NumberFunction(val);
		this.b = new ImageFunction(right);
	}

	public BinaryOperation(final IFunction fn, final Number val) {
		this.a = fn;
		this.b = new NumberFunction(val);
	}

	public BinaryOperation(final Number val, final IFunction fn) {
		this.a = new NumberFunction(val);
		this.b = fn;
	}

	public BinaryOperation(final Number val1, final Number val2) {
		this.a = new NumberFunction(val1);
		this.b = new NumberFunction(val2);
	}

	@Override
	public void findImages(final Set<Image<?>> images) {
		a.findImages(images);
		b.findImages(images);
	}
}