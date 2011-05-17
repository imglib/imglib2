package net.imglib2.ops.observer;


public class IterationTracker implements IterationStatus
{
	public Message message;
	public long[] position;
	public double value;
	public boolean conditionsSatisfied;
	public boolean interruptStatus;

	@Override
	public Message getMessage()
	{
		return message;
	}

	@Override
	public long[] getPosition()
	{
		return position;
	}

	@Override
	public double getValue()
	{
		return value;
	}

	@Override
	public boolean getConditionsSatisfied()
	{
		return conditionsSatisfied;
	}

	@Override
	public boolean wasInterrupted()
	{
		return interruptStatus;
	}
}
