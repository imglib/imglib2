package net.imglib2.ops.condition;

public class Not implements Condition
{
	private final Condition condition;
	
	public Not(final Condition condition)
	{
		this.condition = condition;
	}
	
	@Override
	public boolean isSatisfied(final double value, final long[] position)
	{
		return ! condition.isSatisfied(value, position); 
	}
	
}
