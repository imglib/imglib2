package mpicbg.imglib.scripting.math.fn;


import java.util.Collection;

import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.RealType;

public abstract class BinaryOperation implements IFunction
{
	private final IFunction a, b;

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

	/** Compose: "fn(a1, fn(a2, fn(a3, fn(a4, a5))))".
	 *  Will fail with either {@link IllegalArgumentException} or {@link ClassCastException}
	 *  when the @param elem contains instances of classes other than {@link Image},
	 *  {@link Number}, or {@link IFunction}. */
	public BinaryOperation(final Object... elem) throws Exception {
		this.a = wrap(elem[0]);
		IFunction right = wrap(elem[elem.length-1]);
		for (int i=elem.length-2; i>0; i--) {
			IFunction fn = getClass().getConstructor(new Class<?>[]{IFunction.class, IFunction.class})
					.newInstance(wrap(elem[i]), right);
			right = fn;
		}
		this.b = right;
	}

	static private final IFunction wrap(final Object ob) {
		if (ob instanceof Image<?>) return new ImageFunction((Image<? extends RealType<?>>)ob);
		if (ob instanceof IFunction) return (IFunction)ob;
		if (ob instanceof Number) return new NumberFunction((Number)ob);
		throw new IllegalArgumentException("Cannot compose a function with " + ob);
	}

	@Override
	public final void findCursors(final Collection<Cursor<?>> cursors) {
		a.findCursors(cursors);
		b.findCursors(cursors);
	}
	
	public final IFunction a() { return a; }
	public final IFunction b() { return b; }
}