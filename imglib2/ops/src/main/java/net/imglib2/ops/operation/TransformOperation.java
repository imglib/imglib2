package net.imglib2.ops.operation;

import java.util.Observable;
import java.util.Observer;

import net.imglib2.img.ImgPlus;
import net.imglib2.ops.condition.Condition;
import net.imglib2.ops.observer.IterationTracker;
import net.imglib2.ops.observer.IterationStatus.Message;
import net.imglib2.type.numeric.RealType;

// This class hatched to be used later in ImageJ to speed up some basic
// operations. Avoids some of the performance hits in the general class
// AssignOperation.

//TODO
//performance issues
//1) Regioniterator relied upon. Its uses RandomAccess internally
//   rather than iterators. This is because it can work on a
//   subregion. Imglib needs a fast kind of constrained iterator.
//2) since Condition.isSatisfied() takes a position we get it at
//   every spot. Make Condition rely only on value to avoid.
//   Then only calc position if needed by notifier. Less powerful.
//   Could have ValueCondition, PositionCondition, and FullCondition.
//   Could use the appropriate one as needed. This class could just
//   use a ValueCondition to make this as speedy as possible.
//3) as part of 2) could break out position from RealFunc below.
//   Then wouldn't need to get position at each point. And in fact
//   replace RealFunc with UnaryOperator.
//I guess we need to decide between generality and performance
//  carefully. Maybe make a bunch of interfaces and a few
//  implementations and allow composition where possible.

/**
 * A TransformOperation changes an Img in place by a function. A specialization
 * of AssignOperation for performance reasons.
 * 
 * @author Barry DeZonia
 *
 * @param <T>
 */
public class TransformOperation<T extends RealType<T>> {

	private ImgPlus<T> image;
	private long[] origin;
	private long[] span;
	private RealFunc function;
	private Condition condition;
	private Observable observable;
	private boolean wasInterrupted;

	public interface RealFunc {
		double compute(double value, long[] position);
	}
	
	public TransformOperation(ImgPlus<T> image, RealFunc function) {
		this.image = image;
		this.origin = new long[image.numDimensions()];
		this.span = new long[origin.length];
		image.dimensions(this.span);
		this.function = function;
		this.condition = null;
		this.observable = null;
		this.wasInterrupted = false;
	}
	
	public void addObserver(Observer o)
	{
		if (observable == null)
			observable = new Observable();
		observable.addObserver(o);
	}
	
	public void deleteObserver(Observer o)
	{
		if (observable != null)
		{
			observable.deleteObserver(o);
			
			if (observable.countObservers() == 0)
				observable = null;
		}
	}
	
	public void setRegion(long[] origin, long[] span)
	{
		this.origin = origin.clone();
		this.span = span.clone();
	}
	
	public void setCondition(Condition c)
	{
		this.condition = c;
	}
	
	public void execute()
	{
		Observable notifier = this.observable;  // be thread safe
		
		long[] position = origin.clone();
		
		IterationTracker status = new IterationTracker();
		
		if (notifier != null)
		{
			status.message = Message.INITIALIZE;
			notifier.notifyObservers(status);
		}

		RegionIterator iterator =
			new RegionIterator(image.randomAccess(), origin, span);

		iterator.reset();
		while (iterator.hasNext())
		{
			if (wasInterrupted)
				break;
			
			iterator.next();
			
			double value = Double.NaN;

			iterator.getPosition(position);
			
			boolean conditionSatisfied = true;
			if (condition != null)
				conditionSatisfied = condition.isSatisfied(value, position);

			if (conditionSatisfied) {
				value = function.compute(value, position);
				iterator.setValue(value);
			}
			
			if (notifier != null)
			{
				status.message = Message.UPDATE;
				status.position = position;
				status.value = value;
				status.conditionsSatisfied = conditionSatisfied;
				notifier.notifyObservers(status);
			}
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
}
