/**
 * Copyright (c) 2009--2010, Stephan Preibisch & Stephan Saalfeld
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the Fiji project nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * @author Stephan Preibisch & Stephan Saalfeld
 */

package net.imglib2.img;

import java.util.Iterator;

import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.IterableRealInterval;
import net.imglib2.RandomAccess;

/**
 * A simple container for storing an {@link Img} together with its metadata.
 * Metadata includes name, dimensional axes and calibration information.
 * 
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class ImgPlus<T> implements Img<T>, Metadata {

	private final Img<T> img;

	private final String name;
	private final Axis[] axes;
	private final float[] cal;

	public ImgPlus(final Img<T> img, final Metadata metadata) {
		this(img, metadata.getName(), metadata.getAxes(), metadata
			.getCalibration());
	}

	public ImgPlus(final Img<T> img, final String name, final Axis[] axes,
		final float[] cal)
	{
		this.img = img;
		this.name = name;
		this.axes = axes;
		this.cal = cal;
	}

	public Img<T> getImg() {
		return img;
	}

	// -- Img methods --

	@Override
	public RandomAccess<T> randomAccess() {
		return img.randomAccess();
	}

	@Override
	public RandomAccess<T> randomAccess(final Interval interval) {
		return img.randomAccess(interval);
	}

	@Override
	public int numDimensions() {
		return img.numDimensions();
	}

	@Override
	public long min(final int d) {
		return img.min(d);
	}

	@Override
	public void min(final long[] min) {
		img.min(min);
	}

	@Override
	public long max(final int d) {
		return img.max(d);
	}

	@Override
	public void max(final long[] max) {
		img.max(max);
	}

	@Override
	public void dimensions(final long[] dimensions) {
		img.dimensions(dimensions);
	}

	@Override
	public long dimension(final int d) {
		return img.dimension(d);
	}

	@Override
	public double realMin(final int d) {
		return img.realMin(d);
	}

	@Override
	public void realMin(final double[] min) {
		img.realMin(min);
	}

	@Override
	public double realMax(final int d) {
		return img.realMax(d);
	}

	@Override
	public void realMax(final double[] max) {
		img.realMax(max);
	}

	@Override
	public Cursor<T> cursor() {
		return img.cursor();
	}

	@Override
	public Cursor<T> localizingCursor() {
		return img.localizingCursor();
	}

	@Override
	public long size() {
		return img.size();
	}

	@Override
	public T firstElement() {
		return img.firstElement();
	}

	@Override
	public boolean equalIterationOrder(final IterableRealInterval<?> f) {
		return img.equalIterationOrder(f);
	}

	@Override
	public Iterator<T> iterator() {
		return img.iterator();
	}

	@Override
	public ImgFactory<T> factory() {
		return img.factory();
	}

	// -- Metadata methods --

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Axis[] getAxes() {
		return axes;
	}

	@Override
	public int getAxisIndex(final Axis axis) {
		for (int i = 0; i < axes.length; i++) {
			if (axes[i] == axis) return i;
		}
		return -1;
	}

	@Override
	public float[] getCalibration() {
		return cal;
	}
	
}
