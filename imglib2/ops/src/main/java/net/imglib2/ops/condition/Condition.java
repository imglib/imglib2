package net.imglib2.ops.condition;

import net.imglib2.cursor.LocalizableCursor;
import net.imglib2.type.numeric.RealType;

public interface Condition<T extends RealType<T>>
{
	boolean isSatisfied(final LocalizableCursor<T> cursor, final int[] position);
}

