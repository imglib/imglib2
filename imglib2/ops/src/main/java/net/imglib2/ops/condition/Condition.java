package net.imglib2.ops.condition;

public interface Condition
{
	boolean isSatisfied(final double value, final long[] position);
}

