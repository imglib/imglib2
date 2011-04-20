package net.imglib2.ops.condition;

import net.imglib2.cursor.LocalizableCursor;
import net.imglib2.type.numeric.RealType;


public class AxisLessThan<T extends RealType<T>> implements Condition<T>
{
	private final int axis;
	private final int bound;
	
	public AxisLessThan(final int axis, final int bound)
	{
		this.axis = axis;
		this.bound = bound;
	}
	
	@Override
	public boolean isSatisfied(final LocalizableCursor<T> cursor, final int[] position)
	{
		return position[axis] < bound;
	}
}

