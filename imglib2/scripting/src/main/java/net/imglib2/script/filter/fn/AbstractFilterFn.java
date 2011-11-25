package net.imglib2.script.filter.fn;

import java.util.Collection;

import net.imglib2.IterableRealInterval;
import net.imglib2.RealCursor;
import net.imglib2.script.math.fn.FloatImageOperation;
import net.imglib2.script.math.fn.IFunction;

public abstract class AbstractFilterFn extends FloatImageOperation
{
	/** The algorithm. */
	private final IFunction a;

	public AbstractFilterFn(final IFunction fn) {
		this.a = fn;
	}

	@Override
	public final double eval() {
		return a().eval();
	}

	@Override
	public final void findCursors(final Collection<RealCursor<?>> cursors) {
		a.findCursors(cursors);
	}

	/** Call a().eval() to obtain the result as a double of the computation encapsulated by the {@field a}. 
	 *  @returns the IFunction {@field a} */
	public final IFunction a() { return a; }
	
	@Override
	public void findImgs(final Collection<IterableRealInterval<?>> iris)
	{
		a.findImgs(iris);
	}
}
