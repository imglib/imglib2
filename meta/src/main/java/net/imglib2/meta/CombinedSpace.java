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

package net.imglib2.meta;

import java.util.ArrayList;

import net.imglib2.EuclideanSpace;

/**
 * A {@code CombinedSpace} is a {@link EuclideanSpace} (specifically a
 * {@link TypedSpace}) which is a union of other {@link TypedSpace}s. Common
 * axes are merged as appropriate by matching the {@link AxisType}s of each
 * {@link TypedAxis}.
 * <p>
 * For example, combining three spaces with dimensions (X, Y, Z, CHANNEL), (X,
 * Y, CHANNEL, TIME) and (X, Z, LIFETIME, TIME) will result in a coordinate
 * space with dimensions (X, Y, Z, CHANNEL, TIME, LIFETIME).
 * </p>
 * 
 * @author Curtis Rueden
 */
public class CombinedSpace<A extends TypedAxis, S extends TypedSpace<A>>
	extends ArrayList<S> implements TypedSpace<A>
{

	/** List of axis types for the combined space. */
	private final ArrayList<AxisType> axisTypes = new ArrayList<AxisType>();

	// -- CombinedSpace methods --

	/** Recomputes the combined space based on its current constituents. */
	public void update() {
		synchronized (this) {
			axisTypes.clear();
			for (final TypedSpace<A> space : this) {
				for (int d = 0; d < space.numDimensions(); d++) {
					final AxisType axisType = space.axis(d).type();
					if (!axisTypes.contains(axisType)) {
						// new axis; add to the list
						axisTypes.add(axisType);
					}
				}
			}
		}
	}

	// -- TypedSpace methods --

	@Override
	public int dimensionIndex(final AxisType axis) {
		return axisTypes().indexOf(axis);
	}

	// -- AnnotatedSpace methods --

	@Override
	public A axis(final int d) {
		AxisType type = axisTypes().get(d);

		// find the first axis of a constituent space that matches the type
		for (final TypedSpace<A> space : this) {
			final int id = space.dimensionIndex(type);
			if (id < 0) continue;
			return space.axis(id);
		}
		throw new IllegalStateException("No compatible constituent space");
	}

	@Override
	public void axes(final A[] axes) {
		for (int i = 0; i < axes.length; i++) {
			axes[i] = axis(i);
		}
	}

	@Override
	public void setAxis(final A axis, final int d) {
		final AxisType type = axisTypes().get(d);

		// assign the axis to all constituent spaces of matching type
		for (final TypedSpace<A> space : this) {
			final int id = space.dimensionIndex(type);
			if (id < 0) continue;
			space.setAxis(axis, id);
		}
	}

	// -- EuclideanSpace methods --

	@Override
	public int numDimensions() {
		return axisTypes().size();
	}

	// -- Helper methods --

	/**
	 * Helper method to return axis types in a threadsafe way, so as not to
	 * conflict with {@link #update()}
	 * 
	 * @return list of axis types for this space.
	 */
	private ArrayList<AxisType> axisTypes() {
		synchronized (this) {
			return axisTypes;
		}
	}

}
