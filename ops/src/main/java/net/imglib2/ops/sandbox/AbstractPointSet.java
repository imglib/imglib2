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

import java.util.Iterator;

import net.imglib2.IterableRealInterval;
import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.RealPositionable;
import net.imglib2.type.logic.BoolType;

/**
 * 
 * @author Barry DeZonia
 *
 */
public abstract class AbstractPointSet implements NewPointSet {

	@Override
	public void move(int distance, int d) {
		move((long) distance, d);
	}

	@Override
	public void move(Localizable localizable) {
		for (int i = 0; i < localizable.numDimensions(); i++) {
			move(localizable.getLongPosition(i), i);
		}
	}

	@Override
	public void move(int[] distance) {
		for (int i = 0; i < distance.length; i++) {
			move(distance[i], i);
		}
	}

	@Override
	public void move(long[] distance) {
		for (int i = 0; i < distance.length; i++) {
			move(distance[i], i);
		}
	}

	@Override
	public void setPosition(Localizable localizable) {
		for (int i = 0; i < localizable.numDimensions(); i++) {
			setPosition(localizable.getLongPosition(i), i);
		}
	}

	@Override
	public void setPosition(int[] position) {
		for (int i = 0; i < position.length; i++) {
			setPosition(position[i], i);
		}
	}

	@Override
	public void setPosition(long[] position) {
		for (int i = 0; i < position.length; i++) {
			setPosition(position[i], i);
		}
	}

	@Override
	public void setPosition(int position, int d) {
		setPosition((long)position, d);
	}

	@Override
	public int getIntPosition(int d) {
		return (int) getLongPosition(d);
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
	public BoolType firstElement() {
		return cursor().next();
	}

	@Override
	public Object iterationOrder() {
		return this;
	}

	@Override
	public boolean equalIterationOrder(IterableRealInterval<?> f) {
		return (f == this);
	}

	@Override
	public double realMin(int d) {
		return min(d);
	}

	@Override
	public void realMin(double[] min) {
		for (int i = 0; i < min.length; i++) {
			min[i] = realMin(i);
		}
	}

	@Override
	public void realMin(RealPositionable min) {
		for (int i = 0; i < min.numDimensions(); i++) {
			min.setPosition(realMin(i), i);
		}
	}

	@Override
	public double realMax(int d) {
		return max(d);
	}

	@Override
	public void realMax(double[] max) {
		for (int i = 0; i < max.length; i++) {
			max[i] = realMax(i);
		}
	}

	@Override
	public void realMax(RealPositionable max) {
		for (int i = 0; i < max.numDimensions(); i++) {
			max.setPosition(realMax(i), i);
		}
	}

	@Override
	public Iterator<BoolType> iterator() {
		return cursor();
	}

	@Override
	public void min(long[] min) {
		for (int i = 0; i < min.length; i++) {
			min[i] = min(i);
		}
	}

	@Override
	public void min(Positionable min) {
		for (int i = 0; i < min.numDimensions(); i++) {
			min.setPosition(min(i), i);
		}
	}

	@Override
	public void max(long[] max) {
		for (int i = 0; i < max.length; i++) {
			max[i] = max(i);
		}
	}

	@Override
	public void max(Positionable max) {
		for (int i = 0; i < max.numDimensions(); i++) {
			max.setPosition(max(i), i);
		}
	}

	@Override
	public void dimensions(long[] dimensions) {
		for (int i = 0; i < dimensions.length; i++) {
			dimensions[i] = dimension(i);
		}
	}
}
