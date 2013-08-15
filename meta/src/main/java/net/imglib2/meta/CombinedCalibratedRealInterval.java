package net.imglib2.meta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
