package net.imglib2.ops.condition;

import net.imglib2.Cursor;
import net.imglib2.type.numeric.RealType;


public class ValueGreaterThan<T extends RealType<T>> implements Condition<T>
{
	private final double bound;
	
	public ValueGreaterThan(final double bound)
	{
		this.bound = bound;
	}
	
	@Override
	public boolean isSatisfied(final Cursor<T> cursor, final long[] position)
	{
		return cursor.get().getRealDouble() > bound;
	}
}

