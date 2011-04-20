package net.imglib2.ops.observer;

public interface IterationStatus
{
	enum Message {INITIALIZE, UPDATE, DONE};
	
	Message getMessage();
	int[] getPosition();
	double getValue();
	boolean getConditionsSatisfied();
	boolean wasInterrupted();
}
