package imglib.ops.example.rev3;

import imglib.ops.example.rev3.condition.Condition;
import imglib.ops.example.rev3.function.IntegralScalarFunction;
import imglib.ops.observer.IterationStatus;
import imglib.ops.observer.IterationStatus.Message;

import java.util.Observable;
import java.util.Observer;

import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.special.RegionOfInterestCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.RealType;

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
	private Image<? extends RealType<?>> outputImage;
	private int[] origin;
	private IntegralScalarFunction function;
	private RegionOfInterestCursor<? extends RealType<?>> cursor;
	private boolean wasInterrupted;
	private boolean isDone;
	private Observable notifier;
	private Condition condition;
	
	@SuppressWarnings({"rawtypes","unchecked"})
	public Operation(Image<? extends RealType<?>> outputImage, int[] origin, int[] span, IntegralScalarFunction function)
	{
		this.outputImage = outputImage;
		this.origin = origin;
		this.function = function;
		LocalizableByDimCursor<? extends RealType<?>> tmpCursor = outputImage.createLocalizableByDimCursor();
		this.cursor = new RegionOfInterestCursor(tmpCursor, origin, span);  // nongeneric instantiation. SAFE?
		this.wasInterrupted = false;
		this.isDone = false;
		this.notifier = null;
	}
	
	// Note some inefficiency is execute() for in place transformations (basically because we're maintaining multiple cursors):
	//   we create a cursor on output image (image 1)
	//   we then iterate that cursor and use getPosition() to copy position
	//   we then evaluate the function which eventually points to our input image (again image 1)
	//   it sets its cursor position to position and returns its value

	public void execute()  // TODO - make this run in its own thread. multithread it too?
	{
		int[] position = new int[outputImage.getNumDimensions()];

		Status status = new Status();
		
		if (notifier != null)
		{
			status.message = Message.INITIALIZE;
			notifier.notifyObservers(status);
		}
		
		while (cursor.hasNext())
		{
			if (wasInterrupted)
				break;
			cursor.fwd();
			cursor.getPosition(position);
			for (int i = 0; i < position.length; i++)  // TODO - slowing HACK because RoiCursor returns relative position rather than absolute position
				position[i] += origin[i];
			boolean conditionSatisfied;
			if (condition == null)
				conditionSatisfied = true;
			else
			{
				condition.initEvaluationState();
				conditionSatisfied = condition.isSatisfied(function, position);
			}
			if (conditionSatisfied)
			{
				if ((condition != null) && condition.functionWasFullyEvaluated())
					cursor.getType().setReal(condition.getLastFunctionEvaluation());
				else
					cursor.getType().setReal(function.evaluate(position));
			}
			if (notifier != null)
			{
				status.message = Message.UPDATE;
				status.position = position;
				status.value = cursor.getType().getRealDouble();
				status.conditionsSatisfied = conditionSatisfied;
				notifier.notifyObservers(status);
			}
		}
		
		if (notifier != null)
		{
			status.message = Message.DONE;
			status.wasInterrupted = wasInterrupted;
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
			if (notifier.countObservers() == 0)
				notifier = null;
		}
	}
	
	public void setCondition(Condition c)
	{
		condition = c;
	}
	
	private class Status implements IterationStatus
	{
		Message message;
		int[] position;
		double value;
		boolean conditionsSatisfied;
		boolean wasInterrupted;

		@Override
		public Message getMessage()
		{
			return message;
		}

		@Override
		public int[] getPosition()
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
			return wasInterrupted;
		}
		
	}
}
