/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */


package net.imglib2.ops.function.real;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

// This class arose from a desire for efficiency. In general the size of
// a PointSet can be determined once. But for a ConditionalPointSet
// the size will vary based upon anchor point. Thus can't just allocate
// arrays once and reuse. The safest way to handle this variability in the
// median and trimmed mean calcs is to use List<Double> and clear()
// in the init phase and add() after. But that involves a lot of object
// creation/destruction via autoboxing. So make a primitive array class
// that will safely handle varying size PointSets.
// Note this class runs 2-4 times faster than using a combination of
// ArrayLists using add(), clear(), and Collections.sort() does.

// TODO: write an Img<DoubleType> that replaces this functionality. It should
// be primitive based. It should be growable. Maybe not all memory resident.
// And the key features is that it is sortable in the subset of values present
// (rather than all allocated entries). This is used by median calcs elsewhere.

/**
 * A PrimitiveDoubleArray is a fast replacement for ArrayList<Double>. It
 * avoids autoboxing to improve performance.
 * 
 * @author Barry DeZonia
 *
 */
public class PrimitiveDoubleArray {
	
	// -- instance variables --
	
	private double[] values;
	private int top;
	
	// -- constructor --
	
	public PrimitiveDoubleArray() {
		values = new double[9];
		top = 0;
	}

	// -- public interface --
	
	public void clear() {
		top = 0;
	}
	
	public void add(double v) {
		if (top == values.length) {
			int newLen = top + (top / 2);
			values = Arrays.copyOf(values, newLen);
		}
		values[top++] = v;
	}
	
	public double get(int index) {
		return values[index];
	}
	
	public int size() {
		return top;
	}
	
	public void sortValues() {
		Arrays.sort(values, 0, top);
	}

	// -- private interface --
	
	private static void speedTest() {
		ArrayList<Double> list = new ArrayList<Double>();
		PrimitiveDoubleArray array = new PrimitiveDoubleArray();
		for (int i = 0; i < 1000; i++) {
			list.add((double)i);
			array.add(i);
		}
		Random rng = new Random();

		long start, stop;
		rng.setSeed(100);
		start = System.currentTimeMillis();
		for (int i = 0; i < 500; i++) {
			list.clear();
			for (int j = 0; j < 100; j++) {
				list.add(rng.nextDouble());
			}
			Collections.sort(list);
		}
		stop = System.currentTimeMillis();
		System.out.println("ArrayList time = "+(stop-start));

		rng.setSeed(100);
		start = System.currentTimeMillis();
		for (int i = 0; i < 500; i++) {
			array.clear();
			for (int j = 0; j < 100; j++) {
				array.add(rng.nextDouble());
			}
			array.sortValues();
		}
		stop = System.currentTimeMillis();
		System.out.println("Primitive array time = "+(stop-start));
	}

	// -- test hook --
	
	public static void main(String[] args) {
		speedTest();
	}
}
