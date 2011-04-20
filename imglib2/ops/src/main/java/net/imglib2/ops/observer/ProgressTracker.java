package net.imglib2.ops.observer;

import java.util.Observable;
import java.util.Observer;

public class ProgressTracker implements Observer
{
	private long expectedOperations;
	private long updateFrequency;
	private long operationsSoFar;

	public ProgressTracker(long expectedOperations, long updateFrequency)
	{
		this.expectedOperations = expectedOperations;
		this.updateFrequency = updateFrequency;
	}

	@Override
	public void update(Observable o, Object arg)
	{
		if (arg instanceof IterationStatus)
		{
			IterationStatus status = (IterationStatus) arg;
			
			switch (status.getMessage())
			{
			case INITIALIZE:
				this.operationsSoFar = 0;
				break;
			case UPDATE:
				operationsSoFar++;
				if ((operationsSoFar % updateFrequency) == 0)
				{
					double percentDone = (double)operationsSoFar / (double)expectedOperations;
					// TODO - update progress indicator
				}
				break;
			case DONE:
				// TODO - update progress indicator to show 100% done
				break;
			}
		}
	}
}
