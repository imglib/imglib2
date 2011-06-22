package net.imglib2.ops.condition;

public class Or implements Condition
{
	private final Condition condition1, condition2;
	
	public Or(final Condition condition1, final Condition condition2)
	{
		this.condition1 = condition1;
		this.condition2 = condition2;
	}
	
	@Override
	public boolean isSatisfied(final double value, final long[] position)
	{
		return condition1.isSatisfied(value, position) || condition2.isSatisfied(value, position); 
	}
}
