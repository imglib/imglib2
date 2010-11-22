package mpicbg.imglib.function.operations.op;

import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.integer.LongType;
import mpicbg.imglib.type.numeric.integer.UnsignedByteType;
import mpicbg.imglib.type.numeric.real.DoubleType;
import mpicbg.imglib.type.numeric.real.FloatType;

public abstract class AN< R extends RealType<R> > {

	/*
	// Can't use it: RealType lacks a generic "set" method.
	static private final HashMap<Class<? extends Number>, Class<? extends RealType<?>>> m
		= new HashMap<Class<? extends Number>, Class<? extends RealType<?>>>();
	static {
		m.put(Integer.class, LongType.class); // int as long!
		m.put(Short.class, ShortType.class);
		m.put(Byte.class, UnsignedByteType.class);
		m.put(Long.class, LongType.class);
		m.put(Float.class, FloatType.class);
		m.put(Double.class, DoubleType.class);
	}
	*/

	// TODO: Why does it need the cast?
	protected final RealType<?> asType(final Number val) {
		/*// FAILS
		num = (A) m.get(val.getClass())
		num.set(val);
		*/
		final Class<? extends Number> c = val.getClass();
		if (c == Float.class) return new FloatType(val.floatValue());
		else if (c == Long.class) return new LongType(val.longValue());
		else if (c == Double.class) return new DoubleType(val.doubleValue());
		else if (c == Byte.class) return new UnsignedByteType(val.byteValue());
		else if (c == Integer.class) return new LongType(val.intValue()); // int as long!
		return new FloatType(val.floatValue()); // defaults to float
	}
}
