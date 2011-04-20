package imglib.ops.condition;

import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.type.numeric.RealType;


public class ValueGreaterThan<T extends RealType<T>> implements Condition<T>
{
	private final double bound;
	
	public ValueGreaterThan(final double bound)
	{
		this.bound = bound;
	}
	
	@Override
	public boolean isSatisfied(final LocalizableCursor<T> cursor, final int[] position)
	{
		return cursor.getType().getRealDouble() > bound;
	}
}

