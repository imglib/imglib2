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

package net.imglib2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Abstract base class for {@link AnnotatedSpace} implementations.
 * 
 * @author Curtis Rueden
 */
public abstract class AbstractAnnotatedSpace<A extends Axis> implements
	AnnotatedSpace<A>
{

	private final List<A> axisList;

	public AbstractAnnotatedSpace(final int numDims) {
		axisList = new ArrayList<A>(numDims);
		// We have no way of knowing the axes to populate, so we fill with nulls.
		for (int d = 0; d < numDims; d++) {
			axisList.add(null);
		}
	}

	public AbstractAnnotatedSpace(final A... axes) {
		axisList = Arrays.asList(axes);
	}

	public AbstractAnnotatedSpace(final List<A> axes) {
		axisList = new ArrayList<A>(axes.size());
		axisList.addAll(axes);
	}

	// -- AnnotatedSpace methods --

	@Override
	public A axis(final int d) {
		return axisList.get(d);
	}

	@Override
	public void axes(final A[] axes) {
		for (int d = 0; d < axes.length; d++) {
			axes[d] = axis(d);
		}
	}

	@Override
	public void setAxis(final A axis, final int d) {
		axisList.set(d, axis);
	}

	// -- EuclideanSpace methods --

	@Override
	public int numDimensions() {
		return axisList.size();
	}

}
