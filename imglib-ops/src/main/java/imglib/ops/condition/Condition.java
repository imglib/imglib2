package imglib.ops.condition;

import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.type.numeric.RealType;

public interface Condition<T extends RealType<T>>
{
	boolean isSatisfied(LocalizableCursor<T> cursor, int[] position);
}

