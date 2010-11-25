package mpicbg.imglib.scripting.math2.fn;


import java.util.Collection;

import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.integer.IntType;
import mpicbg.imglib.type.numeric.integer.LongType;
import mpicbg.imglib.type.numeric.integer.ShortType;
import mpicbg.imglib.type.numeric.integer.UnsignedByteType;
import mpicbg.imglib.type.numeric.real.DoubleType;
import mpicbg.imglib.type.numeric.real.FloatType;

public final class NumberFunction implements IFunction {

	private final double val;

	public NumberFunction(final Number num) {
		this.val = NumberFunction.asType(num).getRealDouble();
	}

	@Override
	public final double eval() {
		return val;
	}

	/** Defaults to DoubleType, and treats Byte as unsigned. */
	private static final RealType<?> asType(final Number val) {
		final Class<? extends Number> c = val.getClass();
		if (c == Double.class) return new DoubleType(val.doubleValue());
		else if (c == Long.class) return new LongType(val.longValue());
		else if (c == Float.class) return new FloatType(val.floatValue());
		else if (c == Byte.class) return new UnsignedByteType(val.byteValue());
		else if (c == Integer.class) return new IntType(val.intValue());
		else if (c == Short.class) return new ShortType(val.shortValue());
		return new DoubleType(val.doubleValue());
	}

	@Override
	public final void findCursors(final Collection<Cursor<?>> cursors) {}
}