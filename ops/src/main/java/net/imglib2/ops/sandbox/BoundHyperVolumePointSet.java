package net.imglib2.ops.sandbox;

/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

import net.imglib2.Cursor;
import net.imglib2.FlatIterationOrder;
import net.imglib2.RandomAccess;
import net.imglib2.Sampler;
import net.imglib2.img.Img;
import net.imglib2.type.logic.BoolType;

/**
 * @author Barry DeZonia
 */
public class BoundHyperVolumePointSet extends AbstractPointSet {

	private final int n;
	private final long[] min;
	private final long[] max;
	private final Img<BoolType> img;

	public BoundHyperVolumePointSet(long[] min, long[] max) {
		if (min.length != max.length) {
			throw new IllegalArgumentException("bounds are of differing dimension");
		}
		for (int i = 0; i < min.length; i++) {
			if (max[i] < min[i]) {
				throw new IllegalArgumentException("max point less than min point");
			}
		}
		this.min = min.clone();
		this.max = max.clone();
		n = min.length;
		img = null; // TODO - try to make an ArrayImg<BoolType> - unsupported
		throw new UnsupportedOperationException("Must make an Img<BoolType>");
	}

	@Override
	public boolean contains(long[] point) {
		for (int i = 0; i < n; i++) {
			if (point[i] < min[i]) return false;
			if (point[i] > max[i]) return false;
		}
		return true;
	}

	@Override
	public <T> Cursor<T> bind(RandomAccess<T> randomAccess) {
		return new BoundCursor<T>(randomAccess);
	}

	@Override
	public void fwd(int d) {
		min[d]++;
		max[d]++;
	}

	@Override
	public void bck(int d) {
		min[d]--;
		max[d]--;
	}

	@Override
	public void move(long distance, int d) {
		min[d] += distance;
		max[d] += distance;
	}

	@Override
	public long getLongPosition(int d) {
		return min[d];
	}

	@Override
	public void setPosition(long position, int d) {
		long delta = position - min[d];
		move(delta, d);
	}

	@Override
	public int numDimensions() {
		return n;
	}

	@Override
	public void localize(int[] position) {
		for (int i = 0; i < n; i++)
			position[i] = (int) min[i];
	}

	@Override
	public void localize(long[] position) {
		for (int i = 0; i < n; i++)
			position[i] = min[i];
	}

	@Override
	public void localize(float[] position) {
		for (int i = 0; i < n; i++)
			position[i] = min[i];
	}

	@Override
	public void localize(double[] position) {
		for (int i = 0; i < n; i++)
			position[i] = min[i];
	}

	// ACK CHOKE PUKE FIXME TODO: The problem: calling localize on a bare cursor
	// will not report correct coords after move()'ing this NewPointSet. So we
	// need to share out a PositionCursor instead.

	@Override
	public Cursor<BoolType> cursor() {
		return new PositionCursor();
	}

	@Override
	public Cursor<BoolType> localizingCursor() {
		return cursor();
	}

	@Override
	public long size() {
		if (min.length == 0) return 0;
		long size = 1;
		for (int i = 0; i < n; i++)
			size *= dimension(i);
		return size;
	}

	@Override
	public long min(int d) {
		return min[d];
	}

	@Override
	public long max(int d) {
		return max[d];
	}

	@Override
	public Object iterationOrder() {
		return new FlatIterationOrder(this);
	}

	private class PositionCursor extends AbstractPositionCursor {

		private Cursor<BoolType> cursor;
		private long[] tmpPos;

		@SuppressWarnings("synthetic-access")
		public PositionCursor() {
			cursor = img.localizingCursor();
			tmpPos = new long[cursor.numDimensions()];
		}

		public PositionCursor(PositionCursor other) {
			this();
			cursor = other.cursor.copyCursor();
		}

		@Override
		public int numDimensions() {
			return cursor.numDimensions();
		}

		@SuppressWarnings("synthetic-access")
		@Override
		public void localize(float[] position) {
			cursor.localize(tmpPos);
			for (int i = 0; i < n; i++) {
				position[i] = tmpPos[i] + min[i];
			}
		}

		@SuppressWarnings("synthetic-access")
		@Override
		public void localize(double[] position) {
			cursor.localize(tmpPos);
			for (int i = 0; i < n; i++) {
				position[i] = tmpPos[i] + min[i];
			}
		}

		@Override
		public BoolType get() {
			return cursor.get();
		}

		@Override
		public Sampler<BoolType> copy() {
			return new PositionCursor(this);
		}

		@Override
		public void jumpFwd(long steps) {
			cursor.jumpFwd(steps);
		}

		@Override
		public void fwd() {
			cursor.fwd();
		}

		@Override
		public void reset() {
			cursor.reset();
		}

		@Override
		public boolean hasNext() {
			return cursor.hasNext();
		}

		@Override
		public BoolType next() {
			return cursor.next();
		}

		@SuppressWarnings("synthetic-access")
		@Override
		public void localize(int[] position) {
			cursor.localize(tmpPos);
			for (int i = 0; i < n; i++) {
				position[i] = (int) (tmpPos[i] + min[i]);
			}
		}

		@SuppressWarnings("synthetic-access")
		@Override
		public void localize(long[] position) {
			cursor.localize(tmpPos);
			for (int i = 0; i < n; i++) {
				position[i] = tmpPos[i] + min[i];
			}
		}

		@Override
		public long getLongPosition(int d) {
			return cursor.getLongPosition(d);
		}

		@Override
		public Cursor<BoolType> copyCursor() {
			return new PositionCursor(this);
		}
	}

	private class BoundCursor<T> extends AbstractBoundCursor<T> {

		private Cursor<BoolType> cursor;
		private long[] tmpPos;

		public BoundCursor(final RandomAccess<T> randomAccess)
		{
			super(randomAccess);
			cursor = localizingCursor();
			tmpPos = new long[cursor.numDimensions()];
			rst();
		}

		public BoundCursor(final BoundCursor<T> other) {
			this(other.randomAccess.copyRandomAccess());
			this.cursor = other.cursor.copyCursor();
		}

		@Override
		public int numDimensions() {
			return cursor.numDimensions();
		}

		@Override
		public Cursor<T> copyCursor() {
			return new BoundCursor<T>(this);
		}

		@Override
		public Sampler<T> copy() {
			return new BoundCursor<T>(this);
		}

		@Override
		public void jumpFwd(long steps) {
			cursor.jumpFwd(steps);
			cursor.localize(tmpPos);
			randomAccess.setPosition(tmpPos);
		}

		@Override
		public void fwd() {
			cursor.fwd();
			cursor.localize(tmpPos);
			randomAccess.setPosition(tmpPos);
		}

		@Override
		public void reset() {
			rst();
		}

		@Override
		public boolean hasNext() {
			return cursor.hasNext();
		}

		private void rst() {
			cursor.reset();
			cursor.localize(tmpPos);
			randomAccess.setPosition(tmpPos);
		}
	}
}
