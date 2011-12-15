package net.imglib2.ops.image;

import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.iterator.IntervalIterator;
import net.imglib2.ops.Condition;
import net.imglib2.ops.DiscreteNeigh;
import net.imglib2.ops.PointCondition;
import net.imglib2.ops.UnaryOperation;
import net.imglib2.type.numeric.ComplexType;
import net.imglib2.view.RandomAccessibleIntervalCursor;

public class UnaryOperationalAssignment <T extends ComplexType<T>,
	U extends ComplexType<U>>
{
	private Img<T> img1;
	private Img<U> img2;
	private long[] origin1;
	private long[] origin2;
	private long[] span;
	private UnaryOperation<T,U> op;
	private PointCondition<long[]> inCond;
	private PointCondition<long[]> outCond;
	
	public UnaryOperationalAssignment(Img<T> img1, long[] origin1, Img<U> img2,
			long[] origin2, long[] span, UnaryOperation<T,U> op)
	{
		this.img1 = img1;
		this.img2 = img2;
		this.origin1 = origin1;
		this.origin2 = origin2;
		this.span = span;
		this.op = op;
		this.inCond = null;
		this.outCond = null;
	}

	public void setConditions(PointCondition<long[]> inCond,
			PointCondition<long[]> outCond)
	{
		this.inCond = (inCond == null ? null : inCond.copy());
		this.outCond = (outCond == null ? null : outCond.copy());
	}
	
	public void assign() {
		RandomAccessibleIntervalCursor<T> iCursor = new RandomAccessibleIntervalCursor<T>(interval);
		RandomAccessibleIntervalCursor<U> oCursor = new RandomAccessibleIntervalCursor<U>(interval);
		long[] point = new long[origin1.length];
		while (iCursor.hasNext()) {
			iCursor.fwd();
			oCursor.fwd();
			boolean proceed = true;
			if ((inCond != null) || (outCond != null)) {
				iCursor.localize(point);
				if (inCond != null)
					proceed = inCond.isTrue(point);
				if ((proceed) && (outCond != null))
					proceed = outCond.isTrue(point);
			}
			if (proceed)
				op.compute(iCursor.get(), oCursor.get());
		}
	}
	
	public void abort() {
		// TODO
	}
}
