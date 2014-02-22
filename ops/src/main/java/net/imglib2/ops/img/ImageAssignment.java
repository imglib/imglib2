/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package net.imglib2.ops.img;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.ops.condition.Condition;
import net.imglib2.ops.function.Function;
import net.imglib2.ops.input.InputIterator;
import net.imglib2.ops.input.InputIteratorFactory;
import net.imglib2.ops.pointset.HyperVolumePointSet;
import net.imglib2.type.numeric.ComplexType;

// In old AssignOperation could do many things
// - set conditions on each input and output image
//     Now this can be done by creating a complex Condition
// - set regions of input and output
//     Now this can be done by creating a complex Condition
// - interrupt from another thread
//     Done via abort()
// - observe the iteration
//     still to do
// regions in same image could be handled by a translation function that
//   transforms from one space to another
// regions in different images can also be handled this way
//   a translation function takes a function and a coord transform
// now also these regions, if shape compatible, can be composed into a N+1
//   dimensional space and handled as one dataset
// TODO
// - add listeners in assign (like progress indicators, stat collectors, etc.)
// - rename? It no longer is Img oriented but instead RandomAccessibleInterval.

/**
 * A multithreaded implementation that assigns the values of a region of an
 * RandomAccessibleInterval<OUTPUT> to values from a
 * Function<long[],INTERMEDIATE>. OUTPUT and INTERMEDIATE extend ComplexType<?>.
 * 
 * @author Barry DeZonia
 */
public class ImageAssignment
	<OUTPUT extends ComplexType<OUTPUT>,
		INTERMEDIATE extends ComplexType<INTERMEDIATE>,
		INPUT>
{
	// -- instance variables --

	private ExecutorService executor;
	private boolean assigning;
	private List<Runnable> tasks;
	
	// -- constructor --
	
	/**
	 * Constructor. A working neighborhood is built using negOffs and posOffs. If
	 * they are zero in extent the working neighborhood is a single pixel. This
	 * neighborhood is moved point by point over the
	 * {@link RandomAccessibleInterval} and passed to the function for evaluation.
	 * Pixels are assigned in the {@link RandomAccessibleInterval} if the given
	 * condition is satisfied at that point.
	 * 
	 * @param interval - the {@link RandomAccessibleInterval} to assign data
	 *          values to
	 * @param origin - the origin of the region to assign within the
	 *          {@link RandomAccessibleInterval}
	 * @param span - the extents of the region to assign within the
	 *          {@link RandomAccessibleInterval}
	 * @param function - the Function<INPUT,INTERMEDIATE> to evaluate at each
	 *          point of the region
	 * @param condition - the condition that must be satisfied
	 * @param factory - a factory for generating an input space
	 */
	public ImageAssignment(RandomAccessibleInterval<OUTPUT> interval,
		long[] origin,
		long[] span,
		Function<INPUT,INTERMEDIATE> function,
		Condition<INPUT> condition,
		InputIteratorFactory<INPUT> factory)
	{
		this.assigning = false;
		this.executor = null;
		this.tasks = null;
		setupTasks(interval, origin, span, function, condition, factory);
	}
		
	// -- public interface --

	/**
	 * Assign pixels using input variables specified in constructor. Can be
	 * aborted using abort().
	 */
	public void assign() {
		synchronized(this) {
			assigning = true;
			executor = Executors.newFixedThreadPool(tasks.size());
			for (Runnable task : tasks)
				executor.submit(task);
		}
		boolean terminated = true;
		synchronized (this) {
			// TODO - does this shutdown() call return immediately or wait until
			// everything is complete. If it waits then this synchronized block will
			// keep abort() from being able to work.
			executor.shutdown();
			terminated = executor.isTerminated();
			if (terminated) executor = null;
		}
		while (!terminated) {
			try { Thread.sleep(100); } catch (Exception e) { /* do nothing */ }
			synchronized (this) {
				terminated = executor.isTerminated();
				if (terminated) executor = null;
			}
		}
		synchronized (this) {
			assigning = false;
		}
	}

	/**
	 * Aborts an in progress assignment. Has no effect if not currently
	 * running an assign() operation.
	 */
	public void abort() {
		// TODO - this method maybe ineffective. See TODO note in assign().
		boolean terminated = true;
		synchronized (this) {
			if (!assigning) return;
			if (executor != null) {
				executor.shutdownNow();
				terminated = executor.isTerminated();
			}
		}
		while (!terminated) {
			try { Thread.sleep(100); } catch (Exception e) { /* do nothing */ }
			synchronized (this) {
				if (executor == null)
					terminated = true;
				else
					terminated = executor.isTerminated();
			}
		}
	}

	// -- private helpers --

	private void setupTasks(RandomAccessibleInterval<OUTPUT> interval,
		long[] origin,
		long[] span,
		Function<INPUT,INTERMEDIATE> func,
		Condition<INPUT> cond,
		InputIteratorFactory<INPUT> factory)
	{
		tasks = new ArrayList<Runnable>();
		int axis = chooseBestAxis(span);
		int numThreads = chooseNumThreads(span,axis);
		long length = span[axis] / numThreads;
		if (span[axis] % numThreads > 0) length++;
		long startOffset = 0;
		while (startOffset < span[axis]) {
			if (startOffset + length > span[axis]) length = span[axis] - startOffset;
			Runnable task =
				task(interval, origin, span, axis, origin[axis] + startOffset, length,
					func, cond, factory);
			tasks.add(task);
			startOffset += length;
		}
	}

	/**
	 * Determines best axis to divide along. Currently chooses biggest axis.
	 */
	private int chooseBestAxis(long[] span) {
		int bestAxis = 0;
		long bestAxisSize = span[bestAxis];
		for (int i = 1; i < span.length; i++) {
			long axisSize = span[i]; 
			if (axisSize > bestAxisSize) {
				bestAxis = i;
				bestAxisSize = axisSize;
			}
		}
		return bestAxis;
	}

	/**
	 * Determines how many threads to use
	 */
	private int chooseNumThreads(long[] span, int axis) {
		int maxThreads = Runtime.getRuntime().availableProcessors();
		if (maxThreads == 1) return 1;
		long numElements = numElements(span);
		if (numElements < 10000L) return 1;
		long axisSize = span[axis];
		if (axisSize < maxThreads)
			return (int) axisSize;
		return maxThreads;
	}

	/**
	 * Calculates the number of elements in the output region span
	 */
	private long numElements(long[] span) {
		if (span.length == 0) return 0;
		long numElems = span[0];
		for (int i = 1; i < span.length; i++)
			numElems *= span[i];
		return numElems;
	}

	/** Creates a Runnable task that can be submitted to the thread executor.
	 * The task assigns values to a subset of the output region.
	 */
	private Runnable task(RandomAccessibleInterval<OUTPUT> interval,
		long[] imageOrigin,
		long[] imageSpan,
		int axis,
		long startIndex,
		long length,
		Function<INPUT,INTERMEDIATE> fn,
		Condition<INPUT> cnd,
		InputIteratorFactory<INPUT> factory)
	{
		//System.out.println("axis "+axis+" start "+startIndex+" len "+length);
		final long[] regOrigin = imageOrigin.clone();
		regOrigin[axis] = startIndex;
		final long[] regSpan = imageSpan.clone();
		regSpan[axis] = length;
		
		final long[] regMin = regOrigin;
		final long[] regMax = new long[regMin.length];
		for (int i = 0; i < regMin.length; i++)
			regMax[i] = regMin[i] + regSpan[i] - 1;
		
		HyperVolumePointSet region = new HyperVolumePointSet(regMin, regMax);
		
		// FIXME - warning unavoidable at moment. We don't have the type. If
		// we remove typing from RegionRunner it won't compile.

		return
			new RegionRunner<OUTPUT,INTERMEDIATE>(
interval,
				factory.createInputIterator(region),
				fn.copy(),
				(cnd == null ? null : cnd.copy()));
	}

	/**
	 * RegionRunner is the workhorse for assigning output values from the
	 * evaluation of the input function across a subset of the output region.
	 */
	private class RegionRunner<U extends ComplexType<U>, V extends ComplexType<V>>
		implements Runnable
	{

		private final RandomAccessibleInterval<U> interval;
		private final Function<INPUT, V> function;
		private final Condition<INPUT> condition;
		private final InputIterator<INPUT> iter;

		/**
		 * Constructor
		 */
		public RegionRunner(
RandomAccessibleInterval<U> interval,
			InputIterator<INPUT> iter,
			Function<INPUT, V> func,
			Condition<INPUT> cond)
		{
			this.interval = interval;
			this.function = func;
			this.condition = cond;
			this.iter = iter;
		}

		/**
		 * Conditionally assigns pixels in the output region.
		 */
		@Override
		public void run() {
			final RandomAccess<U> accessor = interval.randomAccess();
			final V output = function.createOutput();
			INPUT input = null;
			while (iter.hasNext()) {
				input = iter.next(input);
				boolean proceed = (condition == null) || (condition.isTrue(input));
				if (proceed) {
					function.compute(input, output);
					accessor.setPosition(iter.getCurrentPoint());
					accessor.get().setReal(output.getRealDouble());
					accessor.get().setImaginary(output.getImaginaryDouble());
					// FIXME
					// Note - for real datasets this imaginary assignment may waste cpu
					// cycles. Perhaps it can get optimized away by the JIT. But maybe not
					// since the type is not really known because this class is really
					// constructed from a raw type. We'd need to test how the JIT handles
					// this situation. Note that in past incarnations this class used
					// assigner classes. The complex version set R & I but the real
					// version just set R. We could adopt that approach once again.
				}
			}
		}
	}
}
