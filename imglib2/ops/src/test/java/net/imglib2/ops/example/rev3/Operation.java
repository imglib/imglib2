package net.imglib2.ops.example.rev3;

import net.imglib2.RandomAccess;
import net.imglib2.ops.example.rev3.constraints.Constraints;
import net.imglib2.ops.example.rev3.function.IntegerIndexedScalarFunction;
import net.imglib2.ops.observer.IterationStatus;
import net.imglib2.ops.observer.IterationStatus.Message;

import java.util.Observable;
import java.util.Observer;

import net.imglib2.ops.operation.RegionIterator;  // TODO - this uses temp Rev2 version
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;

// NOTICE
//   You can copy an image from another image by creating an ImageFunction on the other image and passing it into an Operation.
//   You should be able to setup an ImageFunction with an OutOfBoundsCursor.

// This class will assign to an output image the evaluation of a function across a given domain.

// note that it is possible to modify an image in place by passing it as the output image and also having it be an ImageFunction
// as part of the passed in function. Doing this is safe if the function's relative domain is one pixel in size. These checks are
// not in place at the moment but should be fairly straightforward to do. so right now its possible to try and convolve an image
// in place but realize the input pixels are being modified by the operation. again this points out need for domain information
// for a function. 

public class Operation
{
	private final Img<? extends RealType<?>> outputImage;
	private final IntegerIndexedScalarFunction function;
	private final RegionIterator iterator;
	private boolean wasInterrupted;
	private boolean isDone;
	private Observable notifier;
	private Constraints constraints;
	
	@SuppressWarnings({"rawtypes","unchecked"})
	public Operation(Img<? extends RealType<?>> outputImage, long[] origin, long[] span, IntegerIndexedScalarFunction function)
	{
		this.outputImage = outputImage;
		this.function = function;
		RandomAccess<? extends RealType<?>> tmpAccessor = outputImage.randomAccess();
		this.iterator = new RegionIterator(tmpAccessor, origin, span);  // nongeneric instantiation. SAFE?
		this.wasInterrupted = false;
		this.isDone = false;
		this.notifier = null;
		this.constraints = new Constraints();
	}
	
	// Note some inefficiency is execute() for in place transformations (basically because we're maintaining multiple iterators):
	//   we create a iterator on output image (image 1)
	//   we then iterate that iterator and use getPosition() to copy position
	//   we then evaluate the function which eventually points to our input image (again image 1)
	//   it sets its iterator position to position and returns its value

	public void execute()  // TODO - make this run in its own thread. multithread it too?
	{
		long[] position = new long[outputImage.numDimensions()];

		Status status = new Status();
		
		if (notifier != null)
		{
			status.message = Message.INITIALIZE;
			notifier.notifyObservers(status);
		}
		
		iterator.reset();
		
		while (iterator.hasNext())
		{
			if (wasInterrupted)
				break;
			iterator.next();
			iterator.getPosition(position);
			// next three lines needed for imglib1 but not needed for imglib2
		  // TODO - slowing HACK because RegionOfInterestCursor returns relative position rather than absolute position
			//for (int i = 0; i < position.length; i++)
			//	position[i] += origin[i];
			boolean constraintsSatisfied = constraints.areSatisfied(position);
			if (constraintsSatisfied)
			{
				double newValue = function.evaluate(position);
				iterator.setValue(newValue);
			}
			if (notifier != null)
			{
				status.message = Message.UPDATE;
				status.position = position;
				status.value = iterator.getValue();   // not sure what is best to pass as value if constraints
				status.conditionsSatisfied = constraintsSatisfied;  // violated but I think if I pass original value it might be
				notifier.notifyObservers(status);                   // useful info to caller. its incurs a small performance hit.
			}
		}
		
		if (notifier != null)
		{
			status.message = Message.DONE;
			status.interruptedStatus = wasInterrupted;
			notifier.notifyObservers(status);
		}
		
		isDone = true;
	}
	
	public void interrupt()  // this assumes execute() running in a different thread
	{
		wasInterrupted = true;
	}

	public boolean wasInterrupted()  // this assumes execute() running in a different thread
	{
		return wasInterrupted;
	}
	
	public boolean isDone()  // this assumes execute() running in a different thread
	{
		return isDone;
	}
	
	public void addObserver(Observer ob)
	{
		if (notifier == null)
			notifier = new Observable();
		
		notifier.addObserver(ob);
	}
	
	public void deleteObserver(Observer ob)
	{
		if (notifier != null)
		{
			notifier.deleteObserver(ob);
			// although this could improve performance in execute() it could cause problems there in a multithreaded context
			//if (notifier.countObservers() == 0)
			//	notifier = null;
		}
	}
	
	public void setConstraints(Constraints c)
	{
		constraints = c;
	}
	
	private class Status implements IterationStatus
	{
		Message message;
		long[] position;
		double value;
		boolean conditionsSatisfied;
		boolean interruptedStatus;

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
			return interruptedStatus;
		}
		
	}
}
