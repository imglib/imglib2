package net.imglib2.ops.condition;

import net.imglib2.ops.Condition;
import net.imglib2.ops.Neighborhood;
import net.imglib2.roi.AbstractRegionOfInterest;

public class InsideRoiCondition implements Condition<double[]> {

	// TODO - not abstract === move isMember to RegionOfInterest
	
	private AbstractRegionOfInterest roi;
	
	public InsideRoiCondition(AbstractRegionOfInterest roi) {
		this.roi = roi;
	}
	
	@Override
	public boolean isTrue(Neighborhood<double[]> neigh, double[] point) {
		// return roi.isMember(point); // protected! not public!
		throw new UnsupportedOperationException("unimplemented");
	}

	@Override
	public Condition<double[]> copy() {
		// TODO should copy ROI
		return new InsideRoiCondition(roi);
	}

}
