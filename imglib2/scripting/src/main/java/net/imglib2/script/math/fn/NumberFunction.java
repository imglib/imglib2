package net.imglib2.script.math.fn;


import java.util.Collection;

import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.type.numeric.integer.ShortType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;

/** A function that, when evaluated, always returns the same number,
 *  expressed as a {@code double}.
 *  When given a {@code byte} or a @Byte, it reads it as unsigned. */
public final class NumberFunction implements IFunction {

	private final double val;

	public NumberFunction(final Number num) {
		this.val = NumberFunction.asType(num).getRealDouble();
	}
	
	public NumberFunction(final double val) { this.val = val; }

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

	@Override
	public IFunction duplicate()
	{
		return new NumberFunction(val);
	}

	@Override
	public void findImgs(Collection<Img<?>> imgs) {}
}
