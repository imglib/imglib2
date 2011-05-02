/**
 * Copyright (c) 2011, Stephan Saalfeld
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the imglib project nor
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
 */

package net.imglib2.display;

import java.util.ArrayList;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.converter.Converter;
import net.imglib2.display.XYProjector;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.NumericType;

/**
 * Creates a composite image from across multiple dimensional positions along an
 * axis (typically, but not necessarily, channels). Each dimensional position
 * has its own {@link Converter}. The results of the conversions are summed into
 * the final value. Positions along the axis can be individually toggled for
 * inclusion in the computed composite value using the {@link #setComposite}
 * methods.
 * 
 * @author Curtis Rueden
 * @author Grant Harris
 * @see XYProjector for the code upon which this class was based.
 */
public class CompositeXYProjector<A, B extends NumericType<B>> extends
	XYProjector<A, B>
{

	private final ArrayList<Converter<A, B>> converters;
	private final int dimIndex;
	private final long positionCount;
	private final boolean[] composite;

	public CompositeXYProjector(final Img<A> source,
		final IterableInterval<B> target,
		final ArrayList<Converter<A, B>> converters, final int dimIndex)
	{
		super(source, target, null);
		this.converters = converters;
		this.dimIndex = dimIndex;

		// check that there is one converter per dimensional position
		positionCount = dimIndex < 0 ? 1 : source.dimension(dimIndex);
		final int converterCount = converters.size();
		if (positionCount != converterCount) {
			throw new IllegalArgumentException("Expected " + positionCount +
				" converters but got " + converterCount);
		}

		composite = new boolean[converterCount];
		composite[0] = true;
	}

	// -- CompositeXYProjector methods --

	/** Toggles the given position index's inclusion in composite values. */
	public void setComposite(final int index, final boolean on) {
		composite[index] = on;
	}

	/**
	 * Toggles composite mode globally. If true, all positions along the
	 * dimensional axis are included in the composite; if false, the value will
	 * consist of only the projector's current position (i.e., non-composite
	 * mode).
	 */
	public void setComposite(final boolean on) {
		for (int i = 0; i < composite.length; i++)
			composite[i] = on;
	}

	// -- Projector methods --

	@Override
	public void map() {
		final boolean single = isSingle();
		final Cursor<B> targetCursor = target.cursor();
		final RandomAccess<A> sourceRandomAccess = source.randomAccess();
		sourceRandomAccess.setPosition(position);
		while (targetCursor.hasNext()) {
			final B b = targetCursor.next();
			sourceRandomAccess.setPosition(targetCursor.getLongPosition(0), 0);
			sourceRandomAccess.setPosition(targetCursor.getLongPosition(1), 1);
			b.setZero();
			for (int i = 0; i < positionCount; i++) {
				if (skip(i, single)) continue; // position is excluded from composite
				if (dimIndex >= 0) sourceRandomAccess.setPosition(i, dimIndex);
				final B bi = b.createVariable();
				converters.get(i).convert(sourceRandomAccess.get(), bi);
				b.add(bi); // accumulate converted result
			}
		}
	}

	// -- Helper methods --

	/**
	 * Indicates whether the projector is in single-position mode. This is true
	 * iff all dimensional positions along the composited axis are excluded. In
	 * this case, the current position along that axis is used instead.
	 */
	private boolean isSingle() {
		for (int i = 0; i < composite.length; i++) {
			if (composite[i]) return false;
		}
		return true;
	}

	/**
	 * Indicates whether the given dimensional position should be skipped.
	 * 
	 * @param i The dimensional position along the composited axis.
	 * @param single True if in single-position (i.e., non-composite) mode.
	 * @return True if the position should be excluded from the composite.
	 */
	private boolean skip(final int i, final boolean single) {
		if (composite[i]) return false; // position is included in composite
		if (single && position() == i) return false; // single mode
		return true;
	}

	/** Gets the current position along the composited axis. */
	private long position() {
		return dimIndex < 0 ? 0 : position[dimIndex];
	}

}
