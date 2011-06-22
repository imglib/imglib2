package net.imglib2.ops.condition;

public class ValueGreaterThan implements Condition
{
	private final double bound;
	
	public ValueGreaterThan(final double bound)
	{
		this.bound = bound;
	}
	
	@Override
	public boolean isSatisfied(final double value, final long[] position)
	{
		return value > bound;
	}
}

