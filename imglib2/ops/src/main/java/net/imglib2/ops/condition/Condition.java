package net.imglib2.ops.condition;

import net.imglib2.type.numeric.RealType;

public interface Condition<T extends RealType<T>>
{
	boolean isSatisfied(final T value, final long[] position);
}

