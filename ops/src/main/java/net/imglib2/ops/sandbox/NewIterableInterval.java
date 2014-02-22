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

package net.imglib2.ops.sandbox;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.imglib2.AbstractInterval;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.IterableRealInterval;
import net.imglib2.Sampler;

/**
 * TODO
 *
 */
public class NewIterableInterval<T> extends AbstractInterval implements IterableInterval<T>{

	private List<PrivateCursor<T>> cursors;
	
	public NewIterableInterval(long[] min, long[] max) {
		super(min, max);
		cursors = new ArrayList<PrivateCursor<T>>();
	}

	public void relocate(long[] newOrigin) {
		for (int i = 0; i < newOrigin.length; i++) {
			max[i] += newOrigin[i] - min[i];
			min[i] = newOrigin[i];
		}
	}
	
	@Override
	public long size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public T firstElement() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object iterationOrder()
	{
		// TODO maybe support. For now, for simplicity, don't support
		return this; // iteration order is only compatible with ourselves
	}

	@Override
	public boolean equalIterationOrder( final IterableRealInterval< ? > f )
	{
		return iterationOrder().equals( f.iterationOrder() );
	}

	@Override
	public Iterator<T> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cursor<T> cursor() {
		PrivateCursor<T> cursor = new PrivateCursor<T>(this);
		cursors.add(cursor);
		return cursor;
	}

	@Override
	public Cursor<T> localizingCursor() {
		return new PrivateCursor<T>(this);
	}
	
	private class PrivateCursor<M> implements Cursor<M> {
		private long[] cmin;
		private long[] cmax;
		private long[] cpos;
		private NewIterableInterval<M> interval;
		
		public PrivateCursor(NewIterableInterval<M> interval) {
			this.interval = interval;
			this.cmin = interval.min;
			this.cmax = interval.max;
			this.cpos = cmin.clone();
		}
		
		@Override
		public void localize(float[] position) {
			for (int i = 0; i < n; i++)
				position[i] = cpos[i];
		}

		@Override
		public void localize(double[] position) {
			for (int i = 0; i < n; i++)
				position[i] = cpos[i];
		}

		@Override
		public float getFloatPosition(int d) {
			return getLongPosition(d);
		}

		@Override
		public double getDoublePosition(int d) {
			return getLongPosition(d);
		}

		@Override
		public int numDimensions() {
			return n;
		}

		@Override
		public M get() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Sampler<M> copy() {
			// ARGH
			return null; // new NewIterableInterval<M>(cmin, cmax);
		}

		@Override
		public void jumpFwd(long steps) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void fwd() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void reset() {
			for (int i = 0; i < n; i++) {
				cmin[i] = min[i];
				cmax[i] = max[i];
				cpos[i] = min[i];
			}
		}

		@Override
		public boolean hasNext() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public M next() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void localize(int[] position) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void localize(long[] position) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public int getIntPosition(int d) {
			return (int) getLongPosition(d);
		}

		@Override
		public long getLongPosition(int d) {
			return cpos[d];
		}

		@Override
		public Cursor<M> copyCursor() {
			return new PrivateCursor<M>(interval);
		}
		
	}
}

