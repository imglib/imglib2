package net.imglib2.ops.observer;

public interface IterationStatus
{
	enum Message {INITIALIZE, UPDATE, DONE};
	
	Message getMessage();
	long[] getPosition();
	double getValue();
	boolean getConditionsSatisfied();
	boolean wasInterrupted();
}
