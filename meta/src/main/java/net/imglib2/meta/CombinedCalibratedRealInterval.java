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

package net.imglib2.meta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Barry DeZonia
 */
public class CombinedCalibratedRealInterval<A extends CalibratedAxis, S extends CalibratedRealInterval<A>>
	extends CombinedRealInterval<A, S>
{

	// Units are available one per dim. These are the common units that all other
	// contained CalibratedIntervals convert between.

	private final List<String> units = new ArrayList<String>(super.numDimensions());

	public void setUnit(String unit, int d) {
		units.set(d, unit);
	}

	public String unit(int d) {
		return units.get(d);
	}

	// NB - make sure adding and removing axes maintains units list. We override
	// all the add and remove methods to do so.

	@Override
	public S remove(int index) {
		S val = super.remove(index);
		if (val != null) units.remove(index);
		return val;
	}

	@Override
	public boolean remove(Object o) {
		for (int i = 0; i < size(); i++) {
			if (get(i).equals(o)) {
				return remove(i) != null;
			}
		}
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean changes = false;
		for (Object obj : c) {
			int i = 0;
			while (i < size()) {
				if (get(i) == obj) {
					changes |= (remove(i) != null);
				}
				else i++;
			}
		}
		return changes;
	}

	@Override
	public void add(int index, S element) {
		super.add(index, element);
		units.add(index, null);
	}

	@Override
	public boolean add(S e) {
		super.add(e);
		units.add(null);
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends S> c) {
		boolean changes = false;
		for (S obj : c) {
			changes |= add(obj);
		}
		return changes;
	}

	@Override
	public boolean addAll(int index, Collection<? extends S> c) {
		boolean changes = false;
		Iterator<? extends S> iter = c.iterator();
		int spot = index;
		while (iter.hasNext()) {
			S value = iter.next();
			add(spot++, value);
			changes = true;
		}
		return changes;
	}
}
