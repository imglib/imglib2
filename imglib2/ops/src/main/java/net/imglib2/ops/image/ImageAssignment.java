/*

Copyright (c) 2011, Barry DeZonia.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
  * Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
  * Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
  * Neither the name of the Fiji project developers nor the
    names of its contributors may be used to endorse or promote products
    derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.
*/

package net.imglib2.ops.image;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.ops.Condition;
import net.imglib2.ops.DiscreteNeigh;
import net.imglib2.ops.Function;
import net.imglib2.ops.RegionIndexIterator;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

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


/**
 * A multithreaded implementation that assigns the values of a region of
 * an Img<?> to values from a function.
 *  
 * @author Barry DeZonia
 *
 */
public class ImageAssignment<DATA_TYPE extends RealType<DATA_TYPE>> {

	// -- instance variables --
	
	private final Img<DATA_TYPE> image;
	private final Function<long[],RealType<?>> func;
	private Condition<long[]> cond;
	private final long[] origin;
	private final long[] span;
	private final long[] negOffs;
	private final long[] posOffs;
	private ExecutorService executor;
	private boolean assigning;
	
	// -- constructor --
	
	/**
	 * Constructor. A working neighborhood is built using negOffs and
	 * posOffs. If they are zero in extent the working neighborhood is
	 * a single pixel. This neighborhood is moved point by point over
	 * the Img<?> and passed to the function for evaluation.
	 * 
	 * @param img - the Img<?> to assign data values to
	 * @param origin - the origin of the region to assign within the Img<?>
	 * @param span - the extents of the region to assign within the Img<?>
	 * @param function - the function to evaluate at each point of the region
	 * @param negOffs - the extents in the negative direction of the working neighborhood
	 * @param posOffs - the extents in the positive direction of the working neighborhood
	 * 
	 */
	public ImageAssignment(
		Img<DATA_TYPE> img,
		long[] origin,
		long[] span,
		Function<long[],RealType<?>> function,
		long[] negOffs,
		long[] posOffs)
	{
		this.image = img;
		this.origin = origin.clone();
		this.span = span.clone();
		this.negOffs = negOffs.clone();
		this.posOffs = posOffs.clone();
		this.func = function.copy();
		this.cond = null;
		this.assigning = false;
	}
	
	/**
	 * Constructor. Working neighborhood is assumed to be a single pixel.
	 * This neighborhood is moved point by point over
	 * the Img<?> and passed to the function for evaluation.
	 * 
	 * @param img - the Img<?> to assign data values to
	 * @param origin - the origin of the region to assign within the Img<?>
	 * @param span - the extents of the region to assign within the Img<?>
	 * @param function - the function to evaluate at each point of the region
	 * 
	 */
	public ImageAssignment(
		Img<DATA_TYPE> img,
		long[] origin,
		long[] span,
		Function<long[],RealType<?>> function)
	{
		this.image = img;
		this.origin = origin.clone();
		this.span = span.clone();
		this.negOffs = new long[origin.length];  // ALL ZERO
		this.posOffs = new long[origin.length];  // ALL ZERO
		this.func = function.copy();
		this.cond = null;
		this.assigning = false;
	}
		
	// -- public interface --

	/**
	 * Sets a condition that must be satisfied before each pixel assignment
	 * can take place. The condition is tested at each point in the assignment
	 * region.
	 */
	public void setCondition(Condition<long[]> condition) {
		this.cond = (condition == null ? null : condition.copy());
	}
	
	/**
	 * Assign pixels using input variables specified in constructor. Can be
	 * aborted using abort().
	 */
	public void assign() {
		int axis;
		int numThreads;
		long startOffset;
		long length;
		synchronized(this) {
			assigning = true;
			axis = chooseBestAxis();
			numThreads = chooseNumThreads(axis);
			length = span[axis] / numThreads;
			if (span[axis] % numThreads > 0) length++;
			startOffset = 0;
			executor = Executors.newFixedThreadPool(numThreads);
		}
		while (startOffset < span[axis]) {
			if (startOffset + length > span[axis]) length = span[axis] - startOffset;
			Runnable task =
					task(image, origin, span, axis, origin[axis] + startOffset, length, func, cond, negOffs, posOffs);
			synchronized (this) {
				executor.submit(task);
			}
			startOffset += length;
		}
		boolean terminated = true;
		synchronized (this) {
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
	
	/**
	 * Determines best axis to divide along. Currently chooses biggest axis.
	 */
	private int chooseBestAxis() {
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
	private int chooseNumThreads(int axis) {
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
	private long numElements(long[] sp) {
		if (sp.length == 0) return 0;
		long numElems = sp[0];
		for (int i = 1; i < sp.length; i++)
			numElems *= sp[i];
		return numElems;
	}

	/** Creates a Runnable task that can be submitted to the thread executor.
	 * The task assigns values to a subset of the output region.
	 */
	private Runnable task(
		Img<DATA_TYPE> img,
		long[] imageOrigin,
		long[] imageSpan,
		int axis,
		long startIndex,
		long length,
		Function<long[],RealType<?>> fn,
		Condition<long[]> cnd,
		long[] nOffsets,
		long[] pOffsets)
	{
		//System.out.println("axis "+axis+" start "+startIndex+" len "+length);
		final long[] regOrigin = imageOrigin.clone();
		regOrigin[axis] = startIndex;
		final long[] regSpan = imageSpan.clone();
		regSpan[axis] = length;
		return
			new RegionRunner(
				img,
				regOrigin,
				regSpan,
				fn.copy(),
				(cnd == null ? null : cnd.copy()),
				nOffsets.clone(),
				pOffsets.clone());
	}

	/**
	 * RegionRunner is the workhorse for assigning output values from the
	 * evaluation of the input function across a subset of the output region.
	 */
	private class RegionRunner implements Runnable {
		
		private final Img<DATA_TYPE> img;
		private final Function<long[],RealType<?>> function;
		private final Condition<long[]> condition;
		private final DiscreteNeigh region;
		private final DiscreteNeigh neighborhood;

		/**
		 * Constructor
		 */
		public RegionRunner(
			Img<DATA_TYPE> img,
			long[] origin,
			long[] span,
			Function<long[],RealType<?>> func,
			Condition<long[]> cond,
			long[] negOffs,
			long[] posOffs)
		{
			this.img = img;
			this.function = func;
			this.condition = cond;
 			this.region = buildRegion(origin, span);
			this.neighborhood = new DiscreteNeigh(new long[negOffs.length], negOffs, posOffs);
		}

		/**
		 * Conditionally assigns pixels in the output region.
		 */
		@Override
		public void run() {
			final RandomAccess<DATA_TYPE> accessor = img.randomAccess();
			// TODO COMPLEX
			final DoubleType output = new DoubleType();
			final RegionIndexIterator iter = new RegionIndexIterator(region);
			while (iter.hasNext()) {
				iter.fwd();
				neighborhood.moveTo(iter.getPosition());
				boolean proceed =
						(condition == null) ||
						(condition.isTrue(neighborhood,iter.getPosition()));
				if (proceed) {
					function.evaluate(neighborhood, iter.getPosition(), output);
					accessor.setPosition(iter.getPosition());
					accessor.get().setReal(output.getRealDouble());
					// TODO COMPLEX
					//accessor.get().setImaginary(output.getImaginaryDouble());
				}
			}
		}
		
		/**
		 * Builds a DiscreteNeigh region from an origin and span. The
		 * DiscreteNeigh is needed for use with a RegionIndexIterator.
		 */
		private DiscreteNeigh buildRegion(long[] org, long[] spn) {
			long[] pOffsets = new long[org.length];
			for (int i = 0; i < org.length; i++)
				pOffsets[i] = spn[i] - 1;
			return new DiscreteNeigh(org, new long[org.length], pOffsets);
		}
	}
}
