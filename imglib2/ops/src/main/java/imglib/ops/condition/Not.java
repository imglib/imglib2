package imglib.ops.condition;

import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.type.numeric.RealType;

public class Not<T extends RealType<T>> implements Condition<T>
{
	private final Condition<T> condition;
	
	public Not(final Condition<T> condition)
	{
		this.condition = condition;
	}
	
	@Override
	public boolean isSatisfied(final LocalizableCursor<T> cursor, final int[] position)
	{
		return ! condition.isSatisfied(cursor, position); 
	}
	
}
