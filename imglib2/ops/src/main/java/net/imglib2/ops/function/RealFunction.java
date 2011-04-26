package net.imglib2.ops.function;

import java.util.List;

import net.imglib2.type.numeric.RealType;

public interface RealFunction<T extends RealType<T>>
{
	boolean canAccept(final int numParameters);
	void compute(final List<T> inputs, final T output);
}

