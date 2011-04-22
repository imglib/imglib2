package net.imglib2.ops.condition;

import net.imglib2.Cursor;
import net.imglib2.type.numeric.RealType;


public class AxisLessThan<T extends RealType<T>> implements Condition<T>
{
	private final int axis;
	private final long bound;
	
	public AxisLessThan(final int axis, final long bound)
	{
		this.axis = axis;
		this.bound = bound;
	}
	
	@Override
	public boolean isSatisfied(final Cursor<T> cursor, final long[] position)
	{
		return position[axis] < bound;
	}
}

