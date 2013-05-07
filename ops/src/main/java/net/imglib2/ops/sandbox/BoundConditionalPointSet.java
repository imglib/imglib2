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

package net.imglib2.ops.sandbox;

import net.imglib2.AbstractInterval;
import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.Sampler;
import net.imglib2.ops.condition.Condition;
import net.imglib2.type.logic.BitType;

/**
 * 
 * @author Barry DeZonia
 *
 */
public class BoundConditionalPointSet extends AbstractPointSet {

	private NewPointSet ps;
	private Condition<long[]> condition;
	private boolean needsCalc;
	private long[] min;
	private long[] max;
	private long size;
	
	public BoundConditionalPointSet(NewPointSet ps, Condition<long[]> condition) {
		this.ps = ps;
		this.condition = condition;
		needsCalc = true;
	}
	
	@Override
	public boolean contains(long[] point) {
		return condition.isTrue(point) && ps.contains(point);
	}

	@Override
	public <T> Cursor<T> bind(RandomAccess<T> randomAccess) {
		return new BoundCursor<T>(this, randomAccess);
	}

	@Override
	public void fwd(int d) {
		ps.fwd(d);
		needsCalc = true;
	}

	@Override
	public void bck(int d) {
		ps.bck(d);
		needsCalc = true;
	}

	@Override
	public void move(long distance, int d) {
		ps.move(distance, d);
		needsCalc = true;
	}

	@Override
	public long getLongPosition(int d) {
		return ps.getLongPosition(d);
	}

	@Override
	public void setPosition(long position, int d) {
		ps.setPosition(position, d);
		needsCalc = true;
	}

	@Override
	public int numDimensions() {
		return ps.numDimensions();
	}

	@Override
	public void localize(int[] position) {
		ps.localize(position);
	}

	@Override
	public void localize(long[] position) {
		ps.localize(position);
	}

	@Override
	public void localize(float[] position) {
		ps.localize(position);
	}

	@Override
	public void localize(double[] position) {
		ps.localize(position);
	}

	@Override
	public Cursor<BitType> cursor() {
		return new PositionCursor();
	}

	@Override
	public Cursor<BitType> localizingCursor() {
		return cursor();
	}

	@Override
	public long size() {
		if (needsCalc) calcStuff();
		return size;
	}

	@Override
	public long min(int d) {
		if (needsCalc) calcStuff();
		return min[d];
	}

	@Override
	public long max(int d) {
		if (needsCalc) calcStuff();
		return max[d];
	}

	@Override
	public long dimension(int d) {
		return max(d) - min(d) + 1;
	}

	private void calcStuff() {
		size = 0;
		long[] position = new long[numDimensions()];
		Cursor<BitType> cursor = localizingCursor();
		while (cursor.hasNext()) {
			cursor.next();
			cursor.localize(position);
			for (int i = 0; i < position.length; i++) {
				if (position[i] < min[i]) min[i] = position[i];
				if (position[i] > max[i]) max[i] = position[i];
			}
			size++;
		}
		needsCalc = false;
	}
	
	private class PositionCursor extends AbstractInterval implements Cursor<BitType> {

		private Cursor<BitType> cursor;
		private long[] tmpPos;
		
		public PositionCursor() {
			super(BoundConditionalPointSet.this);
			cursor = ps.cursor();
			tmpPos = new long[n];
		}

		public PositionCursor(PositionCursor other) {
			this();
			long pos = other.cursor.getLongPosition(0); // TODO - looks broken. dim 0 only?
			cursor.jumpFwd(pos+1);
		}
		
		@Override
		public void localize(float[] position) {
			cursor.localize(position);
		}

		@Override
		public void localize(double[] position) {
			cursor.localize(position);
		}

		@Override
		public float getFloatPosition(int d) {
			return cursor.getFloatPosition(d);
		}

		@Override
		public double getDoublePosition(int d) {
			return cursor.getDoublePosition(d);
		}

		@Override
		public BitType get() {
			return cursor.get();
		}

		@Override
		public Sampler<BitType> copy() {
			return cursor();
		}

		@Override
		public void jumpFwd(long steps) {
			for (long l = 0; l < steps; l++)
				position();
		}

		@Override
		public void fwd() {
			// do nothing - hasNext() did the positioning
		}

		@Override
		public void reset() {
			cursor.reset();
		}

		// TODO - this contract a little broken. Repeated hasNext() calls will
		// move internal cursor.
		
		@Override
		public boolean hasNext() {
			return position();
		}

		@Override
		public BitType next() {
			// positioning already done by hasNext()
			return cursor.get();
		}

		@Override
		public void remove() { }

		@Override
		public void localize(int[] position) {
			cursor.localize(position);
		}

		@Override
		public void localize(long[] position) {
			cursor.localize(position);
		}

		@Override
		public int getIntPosition(int d) {
			return cursor.getIntPosition(d);
		}

		@Override
		public long getLongPosition(int d) {
			return cursor.getLongPosition(d);
		}

		@Override
		public Cursor<BitType> copyCursor() {
			return new PositionCursor(this);
		}
		
		private boolean position() {
			while (cursor.hasNext()) {
				cursor.next();
				cursor.localize(tmpPos);
				if (condition.isTrue(tmpPos)) return true;
			}
			return false;
		}
	}
	
	private class BoundCursor<T> extends AbstractBoundCursor<T> {

		private Cursor<BitType> cursor;
		private long[] tmpPos;
		
		public BoundCursor( final Interval interval, final RandomAccess< T > randomAccess )
		{
			super( interval, randomAccess );
			cursor = cursor();
			tmpPos = new long[interval.numDimensions()];
			rst();
		}
		
		protected BoundCursor( final BoundCursor<T> cursor )
		{
			super( cursor, cursor.randomAccess.copyRandomAccess() );
			//long pos = cursor.getLongPosition(d);
			//this.cursor.jumpFwd(pos+1);
			throw new UnsupportedOperationException("TODO");
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
		}

		@Override
		public void fwd() {
			cursor.fwd();
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
