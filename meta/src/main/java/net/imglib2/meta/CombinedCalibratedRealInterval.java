package net.imglib2.meta;

import java.util.ArrayList;
import java.util.List;

public class CombinedCalibratedRealInterval<A extends CalibratedAxis, S extends CalibratedRealInterval<A>>
	extends CombinedRealInterval<A, S>
{

	// Units are available one per dim. These are the common units that all other
	// contained CalibratedIntervals convert between.

	private final List<String> units = new ArrayList<String>();

	public void setUnit(String unit, int d) {
		units.set(d, unit);
	}

	public String unit(int d) {
		return units.get(d);
	}

}
