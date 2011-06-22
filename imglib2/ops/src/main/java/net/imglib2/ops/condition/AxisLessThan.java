package net.imglib2.ops.condition;

public class AxisLessThan implements Condition
{
	private final int axis;
	private final long bound;
	
	public AxisLessThan(final int axis, final long bound)
	{
		this.axis = axis;
		this.bound = bound;
	}
	
	@Override
	public boolean isSatisfied(final double value, final long[] position)
	{
		return position[axis] < bound;
	}
}

