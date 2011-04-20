package imglib.ops.condition;

import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.type.numeric.RealType;

public interface Condition<T extends RealType<T>>
{
	boolean isSatisfied(final LocalizableCursor<T> cursor, final int[] position);
}

