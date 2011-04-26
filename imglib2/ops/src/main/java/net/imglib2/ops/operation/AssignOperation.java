package net.imglib2.ops.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import net.imglib2.ops.condition.Condition;
import net.imglib2.ops.function.RealFunction;
import net.imglib2.ops.observer.IterationStatus;
import net.imglib2.ops.observer.IterationStatus.Message;

import java.util.Observer;

import net.imglib2.type.numeric.RealType;
import net.imglib2.img.Img;

/**
 * An AssignOperation computes values in an output image. The output image is preallocated here. The AssignOperation uses a RealFunction
 *  to compute each pixel value from any number of input images. The passed in function must accept the same number of parameters as the
 *  number of input images given. (Better name welcomed: ImageAssignment? FunctionalEvaluation? Others?)
 * 
 * A user of this class creates an AssignOperation, constrains it as desired, and calls execute(). Note that execute() can be
 * interrupted from another Thread using the quit() method. (this code is currently single threaded).
 * 
 * The operation can be constrained in a number of ways:
 * 
 * - the output image and each input image can be constrained to sample values only within user specified rectangular subregions. All subregions
 * must be shape compatible before calling execute(). See setInputRegion() and setOutputRegion().
 * 
 * - the generation of an output pixel can be further constrained by Conditions - up to one per image. An output pixel is changed at a pixel location
 * only when all Conditions are satisfied. Conditions can be as complex as desired and can be composed of other Conditions. Conditions can even have
 * a spatial component. See setInputCondition() and setOutputCondition().
 * 
 * - the execute() iteration can be observed by any class implementing an Observer interface. A statistical query could be constructed this way.
 * 
 * An image can be repeated multiple times within the input image list. Therefore separate regions of one image can be used as input.
 * 
 * An image can act as both input and output. Thus one can transform an existing image in place.
 * 
 * Limitations
 * 
 *  - the regions are shape compatible. Thus one pixel is computed from some function of all the pixels located in the same place relative
 * to their own images. Thus you can't compute a function on a neighborhood very easily. As it stands you could create an AssignOperation on 9 input
 * images that are the same input image with 9 different subregions. You could then pass a MedianFunction to calculate a median filter. Could be better.
 * (With appropriate out of bounds behavior this is how you could do a convolution too).
 * 
 * - if a user reuses an input image as an output image they have to take care to not change pixels used in further input calculations. Usually this is
 * not a problem unless you are using a spatial Condition or if you have set the output image subregion to partially overlap it's input image region.
 * We could add some checking.
 * 
 * Ideally we'd enhance Imglib such that functions are more integrated in the core. An Image is just a function that maps input coordinates to
 * output values. An image with an out of bounds behavior is a function composed of two functions: the image function and the out of bounds function.
 * A neighborhood is a function that maps input coordinates to output values over a subdomain (thus it should be an Img also). A RealFunction could
 * operate on neighborhoods and return values. Neighborhoods could be within one Image or span multiple Images. But really Image is wrong here
 * as it could just be a function we sample. If a neighborhood can't span multiple images then we'd want ways to compose images out of other images
 * that is all done by reference and delegation. i.e. a 3d composed z stack image made of thirteen 2d images. See how imgib2 supports these concepts.
 */

@SuppressWarnings("unchecked")
public class AssignOperation<T extends RealType<T>>
{
	// -----------------  instance variables ------------------------------------------

	private int imageCount;
	private MultiImageIterator<T> iterator;
	private T outputVariable;
	private long[][] positions;
	private Observable notifier;
	private Condition[] conditions;
	private boolean requireIntersection;
	private RealFunction<T> function;
	private boolean wasInterrupted;

	// -----------------  public interface ------------------------------------------
	
	public AssignOperation(List<Img<T>> inputs, Img<T> output, RealFunction<T> func)
	{
		imageCount = inputs.size() + 1;

		Img<T>[] images = new Img[imageCount];
		images[0] = output;
		for (int i = 1; i <= inputs.size(); i++)
			images[i] = inputs.get(i-1);
		
		iterator = new MultiImageIterator<T>(images);
		
		positions = new long[imageCount][];
		positions[0] = new long[output.numDimensions()];
		for (int i = 1; i < imageCount; i++) {
			positions[i] = new long[inputs.get(i-1).numDimensions()];
		}
		outputVariable = null;
		notifier = null;
		conditions = new Condition[imageCount];
		requireIntersection = true;
		function = func;
		wasInterrupted = false;
		
		if ( ! function.canAccept(inputs.size()) )
			throw new IllegalArgumentException("function cannot handle "+inputs.size()+" input images");
	}

	public void addObserver(Observer o)
	{
		if (notifier == null)
			notifier = new Observable();
		notifier.addObserver(o);
	}
	
	public void deleteObserver(Observer o)
	{
		if (notifier != null)
		{
			notifier.deleteObserver(o);
			
			if (notifier.countObservers() == 0)
				notifier = null;
		}
	}
	
	public void setOutputRegion(long[] origin, long[] span)
	{
		iterator.setRegion(0, origin, span);
	}
	
	public void setOutputCondition(Condition<T> c)
	{
		conditions[0] = c;
	}
	
	public void setInputRegion(int i, long[] origin, long[] span)
	{
		iterator.setRegion(i+1, origin, span);
	}
	
	public void setInputCondition(int i, Condition<T> c)
	{
		conditions[i+1] = c;
	}

	public void intersectConditions()
	{
		requireIntersection = true;
	}
	
	public void unionConditions()
	{
		requireIntersection = false;
	}

	public void execute()
	{
		iterator.initialize();

		RegionCursor<T>[] subCursors = iterator.getCursors();
		
		RegionCursor<T> subCursor0 = subCursors[0];

		outputVariable = subCursor0.getValue();

		List<T> inputVariables = getInputVariables(subCursors);
		
		long[] position = new long[positions[0].length];

		IterationTracker status = new IterationTracker();
		
		if (notifier != null)
		{
			status.message = Message.INITIALIZE;
			notifier.notifyObservers(status);
		}

		while (iterator.isValid())
		{
			if (wasInterrupted)
				break;
			
			double value = Double.NaN;

			boolean conditionsSatisfied = conditionsSatisfied(subCursors); 

			if (conditionsSatisfied)
			{
				function.compute(inputVariables, outputVariable);
				
				value = outputVariable.getRealDouble();
			}
			
			if (notifier != null)
			{
				subCursor0.getPosition(position);
				status.message = Message.UPDATE;
				status.position = position;
				status.value = value;
				status.conditionsSatisfied = conditionsSatisfied;
				notifier.notifyObservers(status);
			}

			iterator.next();
		}

		if (notifier != null)
		{
			status.message = Message.DONE;
			status.interruptStatus = wasInterrupted;
			notifier.notifyObservers(status);
		}
	}

	public void quit()
	{
		wasInterrupted = true;
	}

	// -----------------  private interface ------------------------------------------
	
	private boolean conditionsSatisfied(RegionCursor<T>[] cursors)
	{
		for (int i = 0; i < conditions.length; i++)
		{
			Condition<T> condition = conditions[i];
			
			if (condition == null)
				continue;
			
			RegionCursor<T> subcursor = cursors[i];
			
			subcursor.getPosition(positions[i]);
			
			if (condition.isSatisfied(subcursor.getValue(), positions[i]))
			{
				if (!requireIntersection)  // if union case we can short circuit with success
					return true;
			}
			else // condition not satisfied
			{
				if (requireIntersection)  // in intersection case we can short circuit with failure
					return false;
			}
		}
		
		if (requireIntersection) // intersection - if here everything passed
			return true;
		
		//else union - if here nothing satisfied the condition
		
		return false;
	}

	private List<T> getInputVariables(RegionCursor<T>[] cursors)
	{
		ArrayList<T> variables = new ArrayList<T>();
		
		for (int i = 1; i < imageCount; i++)
			variables.add(cursors[i].getValue());
		
		return variables;
	}

	private class IterationTracker implements IterationStatus
	{
		Message message;
		long[] position;
		double value;
		boolean conditionsSatisfied;
		boolean interruptStatus;

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
}
