package net.imglib2.ops.condition;

import net.imglib2.cursor.LocalizableCursor;
import net.imglib2.type.numeric.RealType;

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
