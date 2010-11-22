package mpicbg.imglib.scripting.math.op;

import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.integer.IntType;
import mpicbg.imglib.type.numeric.integer.LongType;
import mpicbg.imglib.type.numeric.integer.ShortType;
import mpicbg.imglib.type.numeric.integer.UnsignedByteType;
import mpicbg.imglib.type.numeric.real.DoubleType;
import mpicbg.imglib.type.numeric.real.FloatType;

public abstract class AN< R extends RealType<R> >
{
	/** Defaults to DoubleType, and treats Byte as unsigned. */
	protected final RealType<?> asType(final Number val)
	{
		final Class<? extends Number> c = val.getClass();
		if (c == Double.class) return new DoubleType(val.doubleValue());
		else if (c == Long.class) return new LongType(val.longValue());
		else if (c == Float.class) return new FloatType(val.floatValue());
		else if (c == Byte.class) return new UnsignedByteType(val.byteValue());
		else if (c == Integer.class) return new IntType(val.intValue());
		else if (c == Short.class) return new ShortType(val.shortValue());
		return new DoubleType(val.doubleValue());
	}
}