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
import net.imglib2.ops.Real;
import net.imglib2.ops.RegionIndexIterator;
import net.imglib2.type.numeric.RealType;

// In old AssignOperation could do many things
// - set conditions on each input and output image
//     Now this can be done by creating a complex Condition
// - set regions of input and output
//     Now this can be done by creating a complex Condition
// - interrupt from another thread
//     still to do
// - observe the iteration
//     still to do
// regions in same image could be handled by a translation function that
//   transforms from one space to another
// regions in different images can also be handled this way
//   a translation function takes a function and a coord transform
// now also these regions, if shape compatible, can be composed into a N+1
//   dimensional space and handled as one dataset

/**
 * Replacement class for the old OPS' AssignOperation. Assigns the values of
 * a region of an Img<RealType> to values from a function.
 *  
 * @author Barry DeZonia
 *
 */
public class RealImageAssignment {

	// -- instance variables --
	
	private final Img<? extends RealType<?>> img;
	private final Function<long[],Real> func;
	private Condition<long[]> cond;
	private final long[] origin;
	private final long[] span;
	private final long[] negOffs;
	private final long[] posOffs;

	// -- constructor --
	
	public RealImageAssignment(
		Img<? extends RealType<?>> image,
		long[] origin,
		long[] span,
		Function<long[],Real> function,
		long[] negOffs,
		long[] posOffs)
	{
		this.img = image;
		this.origin = origin.clone();
		this.span = span.clone();
		this.negOffs = negOffs.clone();
		this.posOffs = posOffs.clone();
		this.func = function.duplicate();
		this.cond = null;
	}
	
	// -- public interface --
	
	public void setCondition(Condition<long[]> condition) {
		this.cond = condition.duplicate();
	}
	
	// TODO
	// - add listeners (like progress indicators, stat collectors, etc.)
	// - make interruptible
	
	public void assign() {
		int axis = chooseBestAxis();
		int numThreads = chooseNumThreads(axis);
		long length = span[axis] / numThreads;
		if (span[axis] % numThreads > 0) length++;
		long startIndex = origin[axis]; 
		ExecutorService executor = Executors.newFixedThreadPool(numThreads);
		while (startIndex < span[axis]) {
			Runnable task =
					task(img, origin, span, axis, startIndex, length, func, cond, negOffs, posOffs);
			executor.submit(task);
			startIndex += length;
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
			try { Thread.sleep(100); } catch (Exception e) { /* do nothing */ }
		}
	}

	public void abort() {
		// TODO - stop all executing threads
	}

	// -- private helpers --
	
	// right now determines best axis to divide along by returning the biggest axis
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

	private long numElements(long[] sp) {
		if (sp.length == 0) return 0;
		long numElems = sp[0];
		for (int i = 1; i < sp.length; i++)
			numElems *= sp[i];
		return numElems;
	}

	private Runnable task(
		Img<? extends RealType<?>> image,
		long[] imageOrigin,
		long[] imageSpan,
		int axis,
		long startIndex,
		long length,
		Function<long[],Real> fn,
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
					image,
					regOrigin,
					regSpan,
					fn.duplicate(),
					(cnd == null ? null : cnd.duplicate()),
					nOffsets.clone(),
					pOffsets.clone());
	}

	private class RegionRunner implements Runnable {
		
		private final Img<? extends RealType<?>> image;
		private final Function<long[],Real> function;
		private final Condition<long[]> condition;
		private final DiscreteNeigh region;
		private final DiscreteNeigh neighborhood;

		public RegionRunner(
			Img<? extends RealType<?>> image,
			long[] origin,
			long[] span,
			Function<long[],Real> func,
			Condition<long[]> cond,
			long[] negOffs,
			long[] posOffs)
		{
			this.image = image;
			this.function = func;
			this.condition = cond;
 			this.region = buildRegion(origin, span);
			this.neighborhood = new DiscreteNeigh(new long[negOffs.length], negOffs, posOffs);
		}
		
		@Override
		public void run() {
			final RandomAccess<? extends RealType<?>> accessor = image.randomAccess();
			final Real output = function.createOutput();
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
					accessor.get().setReal(output.getReal());
				}
			}
		}
		
		private DiscreteNeigh buildRegion(long[] org, long[] spn) {
			long[] pOffsets = new long[org.length];
			for (int i = 0; i < org.length; i++)
				pOffsets[i] = spn[i] - 1;
			return new DiscreteNeigh(org, new long[org.length], pOffsets);
		}
	}
}
